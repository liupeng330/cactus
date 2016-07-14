package com.qunar.corp.cactus.checker.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.checker.DynamicConfigChecker;
import com.qunar.corp.cactus.checker.ValueChecker;
import com.qunar.corp.cactus.exception.IllegalKeyException;
import com.qunar.corp.cactus.exception.IllegalParamException;
import com.qunar.corp.cactus.exception.LastProviderException;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.util.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static com.alibaba.dubbo.common.Constants.*;
import static com.qunar.corp.cactus.util.ConstantHelper.*;

/**
 * @author zhenyu.nie created on 2013 13-12-9 下午10:17
 */
@Service
public class DynamicConfigCheckerImpl implements DynamicConfigChecker {

    private static final Logger logger = LoggerFactory.getLogger(DynamicConfigCheckerImpl.class);

    @Resource
    private ProviderService providerService;

    @SuppressWarnings("unchecked")
    public void checkValidProvider(ServiceSign sign) {
        Set<String> providerAddresses = providerService
                .getItems(sign.getGroup(), sign.getServiceInterface(), ProviderPredicates.serviceKeyEqual(sign),
                        UrlHelper.zkIdEqual(sign.zkId))
                .transform(UrlHelper.TO_ADDRESS_FUNC).toSet();

        String address = sign.getAddress();
        if (!ANYHOST_VALUE.equals(address) && !providerAddresses.contains(address)) {
            throw new IllegalParamException("address", address);
        }
    }

    public void checkArguments(ServiceSign serviceSign, Map<String, String> params) {
        if (params.isEmpty()) {
            return;
        }
        checkKeys(serviceSign, params);
        checkValues(params);
    }

    public void checkKeys(ServiceSign serviceSign, Map<String, String> params) {
        Set<String> existIllegalKeys = Sets.newHashSet();
        for (String key : params.keySet()) {
            if (isIllegalKey(key, serviceSign)) {
                existIllegalKeys.add(key);
            }
        }
        if (!existIllegalKeys.isEmpty()) {
            throw illegalKeyException(serviceSign, existIllegalKeys);
        }
    }

    private boolean isIllegalKey(final String key, ServiceSign serviceSign) {
        Set<String> illegalKeys = getIllegalKeys(serviceSign);
        return key.startsWith("~") || ValueChecker.HAS_ILLEGAL_CHAR.apply(key)
                || Iterables.any(illegalKeys, new Predicate<String>() {
            public boolean apply(String illegalKey) {
                return illegalKey.equalsIgnoreCase(key);
            }
        });
    }

    private IllegalKeyException illegalKeyException(ServiceSign serviceSign, Set<String> illegalKeys) {
        IllegalKeyException e = new IllegalKeyException(illegalKeys);
        logger.info("conflict for service sign ({}) with keys ({})", serviceSign, illegalKeys, e);
        return e;
    }

    public void checkValues(Map<String, String> params) {
        Map<String, String> illegalParams = Maps.newHashMap();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            Predicate<String> checker = KEY_CHECKER_MAP.get(key);
            if (checker == null) {
                checker = ValueChecker.HAS_NO_ILLEGAL_CHAR;
            } else {
                checker = Predicates.and(checker, ValueChecker.HAS_NO_ILLEGAL_CHAR);
            }
            String value = entry.getValue();
            if (!checker.apply(value)) {
                illegalParams.put(key, value);
            }
        }
        if (!illegalParams.isEmpty()) {
            IllegalParamException e = new IllegalParamException(illegalParams);
            logger.info("illegal params are {}", illegalParams, e);
            throw e;
        }
    }

    public static final Map<String, Predicate<String>> KEY_CHECKER_MAP = Maps.newHashMap();
    static {
        KEY_CHECKER_MAP.put(WEIGHT_KEY, ValueChecker.IS_POSITIVE_NUM);
        KEY_CHECKER_MAP.put(TIMEOUT_KEY, ValueChecker.IS_POSITIVE_NUM);
        KEY_CHECKER_MAP.put(MOCK_KEY, ValueChecker.IS_VALID_MOCK);
        KEY_CHECKER_MAP.put(ENABLED_KEY, ValueChecker.IS_VALID_BOOLEAN);
        KEY_CHECKER_MAP.put(DYNAMIC_KEY, ValueChecker.IS_VALID_BOOLEAN);

        Map<String, Predicate<String>> copy = Maps.newHashMap(KEY_CHECKER_MAP);
        for (Map.Entry<String, Predicate<String>> entry : copy.entrySet()) {
            KEY_CHECKER_MAP.put(DEFAULT_KEY_PREFIX + entry.getKey(), entry.getValue());
        }
    }

    private Set<String> getIllegalKeys(ServiceSign serviceSign) {
        if (Objects.equal(serviceSign.getIp(), Constants.ANYHOST_VALUE)) {
            return ILLEGAL_KEYS_FOR_ANY_HOST;
        } else if (!serviceSign.hasPort()) {
            return ILLEGAL_KEYS_FOR_CONSUMER;
        } else {
            return ILLEGAL_KEYS_FOR_PROVIDER;
        }
    }

    public void checkNotLastProvider(ServiceSign providerSign) {
        Set<URL> providers = providerService.getZkIdAndServiceKeyMatchedEnabledProviders(providerSign).toSortedSet(actualSame());
        if (providers.size() == 1
                && Objects.equal(providers.iterator().next().getAddress(), providerSign.getAddress())) {
            LastProviderException e = new LastProviderException(providerSign);
            logger.warn("try to offline service sign ({}), it is the last provider", providerSign, e);
            throw e;
        }
    }

    private Comparator<URL> actualSame() {
        return new Comparator<URL>() {
            public int compare(URL lhs, URL rhs) {
                if (lhs == null && rhs != null) {
                    return -1;
                } else if (lhs == null) {
                    return 0;
                } else if (rhs == null) {
                    return 1;
                } else {
                    return lhs.getAddress().compareTo(rhs.getAddress());
                }
            }
        };
    }

    private static final Set<String> ILLEGAL_KEYS_FOR_ALL = Sets.newHashSet(DISABLED_KEY, Q_REGISTRY_KEY, VERSION_KEY,
            CATEGORY_KEY, GROUP_KEY, INTERFACE_KEY, ANYHOST_KEY, TIMESTAMP_KEY, CHECK_KEY, SIDE_KEY, DYNAMIC_KEY,
            CLASSIFIER_KEY, CACTUS, CACTUS_TIME, OWNER, RULE_KEY, RUNTIME_KEY, FORCE_KEY, REVISION_KEY, PID_KEY,
            LOADBALANCE_KEY, MOCK_KEY, STUB_KEY, METHODS_KEY, ZKID, CACTUS_ONLINE_STATUS, FAKE_Q_REGISTRY_KEY);

    private static final Set<String> ILLEGAL_KEYS_FOR_PROVIDER = originAndWithDefaultPrefix(Sets.union(
            Sets.newHashSet(APPLICATION_KEY), ILLEGAL_KEYS_FOR_ALL));

    private static final Set<String> ILLEGAL_KEYS_FOR_ANY_HOST = originAndWithDefaultPrefix(Sets.union(
            Sets.newHashSet(APPLICATION_KEY, WEIGHT_KEY), ILLEGAL_KEYS_FOR_ALL));

    private static final Set<String> ILLEGAL_KEYS_FOR_CONSUMER = originAndWithDefaultPrefix(Sets.union(
            Sets.newHashSet(WEIGHT_KEY), ILLEGAL_KEYS_FOR_ALL));

    private static Set<String> originAndWithDefaultPrefix(Set<String> origins) {
        Set<String> result = Sets.newHashSet(origins);
        for (String origin : origins) {
            result.add(DEFAULT_KEY_PREFIX + origin);
        }
        return result;
    }
}
