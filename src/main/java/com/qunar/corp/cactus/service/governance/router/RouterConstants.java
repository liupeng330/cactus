package com.qunar.corp.cactus.service.governance.router;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.util.ConstantHelper;

import java.util.Map;
import java.util.Set;

import static com.alibaba.dubbo.common.Constants.ENABLED_KEY;
import static com.alibaba.dubbo.common.Constants.PRIORITY_KEY;

/**
 * @author zhenyu.nie created on 2013 13-11-29 下午4:55
 */
public class RouterConstants {

    public static final String CONSUMER_APPLICATION = "consumer.application";

    public static final String CONSUMER_MATCH_APP = "consumerMatchApp";

    public static final String CONSUMER_MISMATCH_APP = "consumerMismatchApp";

    public static final String CONSUMER_HOST = "consumer.host";

    public static final String CONSUMER_MATCH_HOST = "consumerMatchIp";

    public static final String CONSUMER_MISMATCH_HOST = "consumerMismatchIp";

    public static final String CONSUMER_METHOD = "consumer.method";

    public static final String MATCH_METHOD = "consumerMatchMethod";

    public static final String MISMATCH_METHOD = "consumerMismatchMethod";

    public static final String PROVIDER_HOST = "provider.host";

    public static final String PROVIDER_MATCH_HOST = "providerMatchIp";

    public static final String PROVIDER_MISMATCH_HOST = "providerMismatchIp";

    public static final String PROVIDER_PORT = "provider.port";

    public static final String PROVIDER_MATCH_PORT = "providerMatchPort";

    public static final String PROVIDER_MISMATCH_PORT = "providerMismatchPort";

    public static final String HOST = "host";

    public static final String PORT = "port";

    public static final Map<String, String> KEY_TO_URL_KEY_MAP = Maps.newHashMapWithExpectedSize(10);

    static {
        KEY_TO_URL_KEY_MAP.put(MATCH_METHOD, CONSUMER_METHOD);
        KEY_TO_URL_KEY_MAP.put(MISMATCH_METHOD, CONSUMER_METHOD);
        KEY_TO_URL_KEY_MAP.put(CONSUMER_MATCH_HOST, CONSUMER_HOST);
        KEY_TO_URL_KEY_MAP.put(CONSUMER_MISMATCH_HOST, CONSUMER_HOST);
        KEY_TO_URL_KEY_MAP.put(CONSUMER_MATCH_APP, CONSUMER_APPLICATION);
        KEY_TO_URL_KEY_MAP.put(CONSUMER_MISMATCH_APP, CONSUMER_APPLICATION);
        KEY_TO_URL_KEY_MAP.put(PROVIDER_MATCH_HOST, PROVIDER_HOST);
        KEY_TO_URL_KEY_MAP.put(PROVIDER_MISMATCH_HOST, PROVIDER_HOST);
        KEY_TO_URL_KEY_MAP.put(PROVIDER_MATCH_PORT, PROVIDER_PORT);
        KEY_TO_URL_KEY_MAP.put(PROVIDER_MISMATCH_PORT, PROVIDER_PORT);
    }

    public static final Set<String> PROVIDER_CONDITION_KEYS = ImmutableSet.of(PROVIDER_HOST, PROVIDER_PORT);

    public static final Set<String> CONSUMER_CONDITION_KEYS = ImmutableSet.of(CONSUMER_METHOD, CONSUMER_HOST, CONSUMER_APPLICATION);

    public static final Set<String> CONSUMER_MATCH_KEYS = ImmutableSet.of(CONSUMER_MATCH_HOST, CONSUMER_MATCH_APP);

    public static final Set<String> WHEN_MATCH_KEYS = ImmutableSet.of(MATCH_METHOD, CONSUMER_MATCH_HOST, CONSUMER_MATCH_APP);

    public static final Set<String> WHEN_MISMATCH_KEYS = ImmutableSet.of(MISMATCH_METHOD, CONSUMER_MISMATCH_HOST, CONSUMER_MISMATCH_APP);

    public static final Set<String> PROVIDER_MATCH_KEYS = ImmutableSet.of(PROVIDER_MATCH_HOST, PROVIDER_MATCH_PORT);

    public static final Set<String> PROVIDER_MISMATCH_KEYS = ImmutableSet.of(PROVIDER_MISMATCH_HOST, PROVIDER_MISMATCH_PORT);

    public static final Set<String> WHEN_KEYS = ImmutableSet.copyOf(Sets.union(WHEN_MATCH_KEYS, WHEN_MISMATCH_KEYS));

    public static final Set<String> METHOD_KEYS = ImmutableSet.of(MATCH_METHOD, MISMATCH_METHOD);

    public static final Set<String> CONSUMER_KEYS = ImmutableSet.copyOf(Sets.difference(WHEN_KEYS, METHOD_KEYS));

    public static final Set<String> PROVIDER_KEYS = ImmutableSet.copyOf(Sets.union(PROVIDER_MATCH_KEYS, PROVIDER_MISMATCH_KEYS));

    public static final Set<String> MATCH_KEYS = ImmutableSet.copyOf(Sets.union(WHEN_MATCH_KEYS, PROVIDER_MATCH_KEYS));

    public static final Set<String> MISMATCH_KEYS = ImmutableSet.copyOf(Sets.union(WHEN_MISMATCH_KEYS, PROVIDER_MISMATCH_KEYS));

    public static final Set<String> MATCH_AND_MISMATCH_KEYS = ImmutableSet.copyOf(Sets.union(MATCH_KEYS, MISMATCH_KEYS));

    public static final Set<String> OTHER_KEYS = ImmutableSet.of(ConstantHelper.NAME, PRIORITY_KEY, ENABLED_KEY, ConstantHelper.STATUS, ConstantHelper.DATA_TYPE, ConstantHelper.ZKID);

    public static final Set<String> KEYS_WITHOUT_PROVIDER_KEYS = ImmutableSet.copyOf(Sets.union(OTHER_KEYS, WHEN_KEYS));

    public static final Set<String> KEYS = ImmutableSet.copyOf(Sets.union(PROVIDER_KEYS, KEYS_WITHOUT_PROVIDER_KEYS));
}
