package com.qunar.corp.cactus.service.governance.router;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.util.ConstantHelper;

import java.util.Map;

import static com.alibaba.dubbo.common.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author zhenyu.nie created on 2013 13-12-8 下午6:37
 */
public class RouterParam {

    public static final Map<String, String> MATCH_URL_KEY_TO_KEY_MAP = Maps.newHashMapWithExpectedSize(5);

    static {
        MATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.CONSUMER_METHOD, RouterConstants.MATCH_METHOD);
        MATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.CONSUMER_HOST, RouterConstants.CONSUMER_MATCH_HOST);
        MATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.CONSUMER_APPLICATION, RouterConstants.CONSUMER_MATCH_APP);
        MATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.PROVIDER_HOST, RouterConstants.PROVIDER_MATCH_HOST);
        MATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.PROVIDER_PORT, RouterConstants.PROVIDER_MATCH_PORT);
    }

    public static final Map<String, String> MISMATCH_URL_KEY_TO_KEY_MAP = Maps.newHashMapWithExpectedSize(5);

    static {
        MISMATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.CONSUMER_METHOD, RouterConstants.MISMATCH_METHOD);
        MISMATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.CONSUMER_HOST, RouterConstants.CONSUMER_MISMATCH_HOST);
        MISMATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.CONSUMER_APPLICATION, RouterConstants.CONSUMER_MISMATCH_APP);
        MISMATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.PROVIDER_HOST, RouterConstants.PROVIDER_MISMATCH_HOST);
        MISMATCH_URL_KEY_TO_KEY_MAP.put(RouterConstants.PROVIDER_PORT, RouterConstants.PROVIDER_MISMATCH_PORT);
    }

    public static final Map<String, Predicate<String>> KEY_CHECKER_MAP = Maps.newHashMap();

    static {
        KEY_CHECKER_MAP.put(ConstantHelper.NAME, Checkers.IS_NON_NULL_OR_EMPTY);
        KEY_CHECKER_MAP.put(PRIORITY_KEY, Checkers.IS_VALID_PRIORITY);
        KEY_CHECKER_MAP.put(ENABLED_KEY, Checkers.IS_VALID_ENABLED);
        for (String routeProperty : RouterConstants.MATCH_AND_MISMATCH_KEYS) {
            KEY_CHECKER_MAP.put(routeProperty, Checkers.TRIVIAL_OR_AT_MOST_ONE_ASTERISK_BETWEEN_COMMA);
        }
    }

    static class Checkers {
        static final Predicate<String> IS_NON_NULL_OR_EMPTY = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return !isNullOrEmpty(input);
            }
        };

        static final Predicate<String> IS_VALID_PRIORITY = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                if (isNullOrEmpty(input)) {
                    return true;
                }
                try {
                    Integer.valueOf(input);
                } catch (Throwable e) {
                    return false;
                }
                return true;
            }
        };

        static final Predicate<String> TRIVIAL_OR_AT_MOST_ONE_ASTERISK_BETWEEN_COMMA = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                if (isNullOrEmpty(input)) {
                    return true;
                }
                return !Iterables.any(Splitter.on(",").trimResults().omitEmptyStrings().split(input),
                        MORE_THAN_ONE_ASTERISK);
            }
        };

        static final Predicate<String> IS_VALID_ENABLED = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return Strings.isNullOrEmpty(input) || "true".equals(input) || "false".equals(input);
            }
        };


        static final Predicate<String> MORE_THAN_ONE_ASTERISK = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                int indexOfAsterisk = input.indexOf(ANY_VALUE);
                return indexOfAsterisk > 0 && input.substring(indexOfAsterisk + 1).contains(ANY_VALUE);
            }
        };
    }
}
