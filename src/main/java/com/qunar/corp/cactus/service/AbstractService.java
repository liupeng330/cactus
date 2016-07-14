package com.qunar.corp.cactus.service;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Configurator;
import com.alibaba.dubbo.rpc.cluster.configurator.override.OverrideConfigurator;
import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.DataType;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.Status;

import javax.annotation.Resource;
import java.util.*;

import static com.alibaba.dubbo.common.Constants.ANY_VALUE;
import static com.alibaba.dubbo.common.Constants.APPLICATION_KEY;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.qunar.corp.cactus.util.ConstantHelper.CONDITION_KEYS;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-11-13
 * Time: 下午7:01
 */
public abstract class AbstractService {

    @Resource
    protected ZKClusterService zkClusterService;

    protected String getDefaultValueWhenNull(String value, String defaultValue) {
        return Strings.isNullOrEmpty(value) ? defaultValue : value;
    }

    protected List<Configurator> toConfigurators(Iterable<URL> urls) {
        return URL_TO_CONFIG_FUNC.apply(urls);
    }

    private static final Function<Iterable<URL>, List<Configurator>> URL_TO_CONFIG_FUNC = new Function<Iterable<URL>, List<Configurator>>() {
        @Override
        public List<Configurator> apply(Iterable<URL> urls) {
            List<Configurator> configurators = new ArrayList<Configurator>();
            for (URL url : urls) {
                if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol())) {
                    configurators.clear();
                    break;
                }
                Map<String, String> override = new HashMap<String, String>(url.getParameters());
                // override 上的anyhost可能是自动添加的，不能影响改变url判断
                override.remove(Constants.ANYHOST_KEY);
                if (override.size() == 0) {
                    configurators.clear();
                    continue;
                }
                configurators.add(new OverrideConfigurator(url));
            }
            Collections.sort(configurators);
            return configurators;
        }
    };

    protected Predicate<GovernanceData> hasId(final Long id) {
        return new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                return input.getId().equals(id);
            }
        };
    }

    protected Predicate<GovernanceData> isDataEnable() {
        return IS_ENABLED_FUNC;
    }

    protected Predicate<GovernanceData> isZkData() {
        return IS_ZK_DATA;
    }

    protected Predicate<GovernanceData> isUserData() {
        return IS_USER_DATA;
    }

    protected Predicate<GovernanceData> isDataType(final DataType dataType) {
        return new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                return Objects.equal(dataType, input.getDataType());
            }
        };
    }

    private static final Predicate<GovernanceData> IS_USER_DATA = new Predicate<GovernanceData>() {
        @Override
        public boolean apply(GovernanceData input) {
            return input.getDataType().code == DataType.USERDATA.code;
        }
    };

    private static final Predicate<GovernanceData> IS_ZK_DATA = new Predicate<GovernanceData>() {
        @Override
        public boolean apply(GovernanceData input) {
            return input.getDataType().code == DataType.ZKDATA.code;
        }
    };


    private static final Predicate<GovernanceData> IS_ENABLED_FUNC = new Predicate<GovernanceData>() {
        @Override
        public boolean apply(GovernanceData input) {
            return Objects.equal(Status.ENABLE, input.getStatus());
        }
    };

    protected Predicate<GovernanceData> hasAddress(final String address) {
        return new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                return Objects.equal(address, input.getUrl().getAddress());
            }
        };
    }

    protected Predicate<GovernanceData> matchApp(final String app) {
        return new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                URL url = input.getUrl();
                String configApp = url.getParameter(APPLICATION_KEY, url.getUsername());
                return configApp == null || ANY_VALUE.equals(configApp) || configApp.equals(app);
            }
        };
    }

    protected Predicate<GovernanceData> hasKeyValue(final String key, final String value) {
        return new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                URL url = input.getUrl();
                return Objects.equal(nullToEmpty(value), nullToEmpty(url.getParameter(key)));
            }
        };
    }

    protected Predicate<GovernanceData> hasNoKeyOf(final String key) {
        return new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                URL url = input.getUrl();
                return isNullOrEmpty(url.getParameter(key));
            }
        };
    }

    protected Predicate<GovernanceData> hasKeyOf(String key) {
        return Predicates.not(hasNoKeyOf(key));
    }

    protected Map<String, String> removeEmptyParams(Map<String, String> params) {
        Map<String, String> realParams = Maps.newHashMap();
        if (params == null || params.isEmpty()) {
            return realParams;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = nullToEmpty(entry.getKey()).trim();
            String value = COMMA_JOINER.join(COMMA_SPLITTER.split(nullToEmpty(entry.getValue())));
            if (!isNullOrEmpty(key) && !isNullOrEmpty(value)) {
                realParams.put(key, value);
            }
        }
        return realParams;
    }

    protected static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    protected static final Joiner COMMA_JOINER = Joiner.on(',');

    protected boolean isTrivialParams(Map<String, String> realParams) {
        return FluentIterable.from(realParams.keySet()).allMatch(Predicates.in(CONDITION_KEYS));
    }
}
