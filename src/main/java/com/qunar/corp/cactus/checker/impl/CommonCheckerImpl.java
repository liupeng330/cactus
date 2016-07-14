package com.qunar.corp.cactus.checker.impl;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.checker.CommonChecker;
import com.qunar.corp.cactus.exception.DubboVersionException;
import com.qunar.corp.cactus.exception.UnSupportProviderException;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.DelayedString;
import com.qunar.corp.cactus.util.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.alibaba.dubbo.common.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.qunar.corp.cactus.util.ConstantHelper.NEW_LINE;
import static com.qunar.corp.cactus.util.UrlHelper.hasKeyOf;

/**
 * @author zhenyu.nie created on 2013 13-11-29 下午5:09
 */
@Service
public class CommonCheckerImpl implements CommonChecker {

    private static final Logger logger = LoggerFactory.getLogger(CommonCheckerImpl.class);

    @Resource
    private ProviderService providerService;

    @Resource
    private ConsumerService consumerService;

    private volatile DubboVersion dubboVersion;

    private static class DubboVersion {
        final int first;
        final int second;
        final int third;
        final String str;

        private DubboVersion(int first, int second, int third) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.str = first + "." + second + "." + third;
        }

        @Override
        public String toString() {
            return "DubboVersion{" +
                    "first=" + first +
                    ", second=" + second +
                    ", third=" + third +
                    ", str='" + str + '\'' +
                    '}';
        }
    }

    @PostConstruct
    public void after() {
        MapConfig mapConfig = MapConfig.get("dubboversion.properties");
        mapConfig.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            public void onLoad(Map<String, String> params) {
                dubboVersion = checkAndGetDubboVersion(params);
                logger.info("load least dubbo version: [{}]", dubboVersion.str);
            }
        });
        Preconditions.checkNotNull(dubboVersion, "can not load least dubbo version");
    }

    private DubboVersion checkAndGetDubboVersion(Map<String, String> params) {
        int first = Integer.parseInt(params.get(VERSION_FIRST));
        int second = Integer.parseInt(params.get(VERSION_SECOND));
        int third = Integer.parseInt(params.get(VERSION_THIRD));
        Preconditions.checkArgument(first >= 0);
        Preconditions.checkArgument(second >= 0);
        Preconditions.checkArgument(third >= 0);
        return new DubboVersion(first, second, third);
    }

    public void checkValidState(ServiceSign sign) {
        checkValidProvider(sign);
    }

    private void checkValidProvider(ServiceSign sign) {
        List<URL> unSupportProviders = getUnSupportProviders(sign);
        if (!unSupportProviders.isEmpty()) {
            throw new UnSupportProviderException(unSupportProviders, "do not support static provider, "
                    + "providers in group " + sign.getGroup() + " and interface (" + sign.serviceInterface + ")"
                    + NEW_LINE + DelayedString.toString(unSupportProviders, UrlHelper.URL_TO_FULL_STRING_FUNC));
        }
    }

    @SuppressWarnings("unchecked")
    private List<URL> getUnSupportProviders(ServiceSign sign) {
        return providerService.getItems(sign.getGroup(), sign.serviceInterface, UrlHelper.zkIdEqual(sign.zkId))
                .filter(hasVersionMatchConsiderAsterisk(sign.version))
                .filter(isUnSupportProvider()).toList();
    }

    private Predicate<URL> hasVersionMatchConsiderAsterisk(final String version) {
        return new Predicate<URL>() {
            public boolean apply(URL input) {
                String inputVersion = input.getParameter(VERSION_KEY);
                return ANY_VALUE.equals(version) || ANY_VALUE.equals(inputVersion)
                        || nullToEmpty(version).equals(nullToEmpty(inputVersion));
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Predicate<URL> isUnSupportProvider() {
        return Predicates.or(IS_STATIC_PROVIDER, WITH_ILLEGAL_GROUP, WITH_ILLEGAL_VERSION,
                DEFAULT_DISABLED, DEFAULT_NOT_ENABLED_FUNC, hasKeyOf(CLASSIFIER_KEY));
    }

    public void checkDubboVersion(ServiceSign sign) {
        List<URL> improperNodes = getImproperDubboVersionNodes(sign);
        if (!improperNodes.isEmpty()) {
            throw new DubboVersionException(dubboVersion.str, improperNodes, "improper dubbo version with group "
                    + sign.getGroup() + " and service key " + sign.getServiceKey() + " in providers and consumers, "
                    + "dubbo version should be greater or equal to " + dubboVersion.str + " :" + NEW_LINE
                    + DelayedString.toString(improperNodes, UrlHelper.URL_TO_FULL_STRING_FUNC));
        }
    }

    @SuppressWarnings("unchecked")
    private List<URL> getImproperDubboVersionNodes(ServiceSign sign) {
        Iterable<URL> concat = consumerService.getItems(sign.getGroup(), sign.serviceInterface, UrlHelper.zkIdEqual(sign.zkId));
        return FluentIterable
                .from(concat)
                .filter(hasVersionMatchConsiderAsterisk(sign.version))
                .filter(lowerVersionThan(dubboVersion))
                .toList();
    }

    private static final String VERSION_FIRST = "version.first";
    private static final String VERSION_SECOND = "version.second";
    private static final String VERSION_THIRD = "version.third";

    private Predicate<URL> lowerVersionThan(final DubboVersion dubboVersion) {
        return new Predicate<URL>() {
            public boolean apply(URL input) {
                String version = input.getParameter(DUBBO_VERSION_KEY);
                if (isNullOrEmpty(version)) {
                    return true;
                }

                try {
                    version = removeSnapshotSuffix(version);

                    int indexOfFirstComma = version.indexOf('.');
                    if (indexOfFirstComma < 0) {
                        return true;
                    }

                    int indexOfSecondComma = version.indexOf('.', indexOfFirstComma + 1);
                    if (indexOfSecondComma < 0) {
                        return true;
                    }

                    int first = Integer.parseInt(version.substring(0, indexOfFirstComma));
                    int second = Integer.parseInt(version.substring(indexOfFirstComma + 1, indexOfSecondComma));
                    int third = Integer.parseInt(version.substring(indexOfSecondComma + 1));

                    return !(first > dubboVersion.first
                            || first == dubboVersion.first && second > dubboVersion.second
                            || first == dubboVersion.first && second == dubboVersion.second && third >= dubboVersion.third);
                } catch (Exception e) {
                    logger.warn("load provider level node dubbo version error, version=[{}]", version);
                    return true;
                }
            }
        };
    }

    private String removeSnapshotSuffix(String version) {
        int indexOfSnapshot = version.toLowerCase().indexOf("-snapshot");
        return indexOfSnapshot >= 0 ? version.substring(0, indexOfSnapshot) : version;
    }

    private static final Predicate<URL> DEFAULT_DISABLED = new Predicate<URL>() {
        public boolean apply(URL input) {
            return input.getParameter(DISABLED_KEY, false);
        }
    };

    private static final Predicate<URL> DEFAULT_NOT_ENABLED_FUNC = new Predicate<URL>() {
        public boolean apply(URL input) {
            return !input.getParameter(ENABLED_KEY, true);
        }
    };

    private static final Predicate<URL> IS_STATIC_PROVIDER = new Predicate<URL>() {
        public boolean apply(URL input) {
            return !input.getParameter(DYNAMIC_KEY, true);
        }
    };

    private static final Predicate<URL> WITH_ILLEGAL_GROUP = new Predicate<URL>() {
        public boolean apply(URL input) {
            String serviceGroup = input.getParameter(GROUP_KEY);
            return !isNullOrEmpty(serviceGroup) &&
                    (serviceGroup.equals(ANYHOST_VALUE) || serviceGroup.contains(","));
        }
    };

    private static final Predicate<URL> WITH_ILLEGAL_VERSION = new Predicate<URL>() {
        public boolean apply(URL input) {
            return ANY_VALUE.equals(input.getParameter(VERSION_KEY));
        }
    };
}
