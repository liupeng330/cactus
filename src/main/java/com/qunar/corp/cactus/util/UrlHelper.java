/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.util;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.List;

import static com.alibaba.dubbo.common.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.qunar.corp.cactus.util.ConstantHelper.FAKE_Q_REGISTRY_KEY;
import static com.qunar.corp.cactus.util.ConstantHelper.Q_REGISTRY_KEY;

/**
 * @author zhenyu.nie created on 2013 13-11-27 下午3:43
 */
public class UrlHelper {

    public static Function<GovernanceData, URL> toFullUrl() {
        return TO_FULL_URL_FUNC;
    }

    private static final Function<GovernanceData, URL> TO_FULL_URL_FUNC = new Function<GovernanceData, URL>() {
        @Override
        public URL apply(GovernanceData input) {
            return input.getFullUrl();
        }
    };

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    public static List<String> getMethods(URL url) {
        String methodsStr = Strings.nullToEmpty(url.getParameter(METHODS_KEY));
        return ImmutableList.copyOf(FluentIterable
                .from(COMMA_SPLITTER.split(methodsStr))
                .filter(Predicates.not(Predicates.equalTo(ANY_VALUE))).toSet());
    }

    public static Predicate<URL> hasKeyOf(String key) {
        return Predicates.not(hasNoKeyOf(key));
    }

    public static Predicate<URL> hasNoKeyOf(final String key) {
        return new Predicate<URL>() {
            @Override
            public boolean apply(URL input) {
                return isNullOrEmpty(input.getParameter(key));
            }
        };
    }

    public static Predicate<URL> matchSubscriber(final URL subscriber) {
        return new Predicate<URL>() {
            @Override
            public boolean apply(URL input) {
                return UrlUtils.isMatch(subscriber, input);
            }
        };
    }

    public static Predicate<URL> matchServiceKey(final ServiceSign sign) {
        return new Predicate<URL>() {
            @Override
            public boolean apply(URL input) {
                String inputInterface = nullToEmpty(input.getServiceInterface());
                String serviceGroup = nullToEmpty(input.getParameter(GROUP_KEY));
                String version = nullToEmpty(input.getParameter(VERSION_KEY));
                return inputInterface.equals(sign.serviceInterface)
                        && (serviceGroup.equals("*") || serviceGroup.equals(sign.serviceGroup)
                        || StringUtils.isContains(serviceGroup, sign.serviceGroup))
                        && (version.equals("*") || version.equals(sign.version));
            }
        };
    }

    public static Function<URL, String> toKey(final String key) {
        return new Function<URL, String>() {
            @Override
            public String apply(URL input) {
                return nullToEmpty(input.getParameter(key));
            }
        };
    }

    public static final Function<URL, String> URL_TO_FULL_STRING_FUNC = new Function<URL, String>() {
        @Override
        public String apply(URL input) {
            return input.toFullString();
        }
    };

    public static Predicate<URL> zkIdEqual(long zkId) {
        return new ZkIdPredicate(zkId);
    }

    public static class ZkIdPredicate implements Predicate<URL> {
        private final long zkId;

        public ZkIdPredicate(long zkId) {
            this.zkId = zkId;
        }

        @Override
        public boolean apply(URL input) {
            return String.valueOf(zkId).equals(input.getParameter(ConstantHelper.ZKID));
        }
    }

    public static Predicate<URL> hasProtocol(final String protocol) {
        return new Predicate<URL>() {
            @Override
            public boolean apply(URL input) {
                return Objects.equal(input.getProtocol(), protocol);
            }
        };
    }

    public static String getGroup(URL url) {
        return PathUtil.makePath(url.getParameter(FAKE_Q_REGISTRY_KEY, url.getParameter(Q_REGISTRY_KEY)));
    }

    public static Predicate<URL> hasKeyValue(final String key, final String value) {
        return new Predicate<URL>() {
            @Override
            public boolean apply(URL url) {
                return Objects.equal(nullToEmpty(value), nullToEmpty(url.getParameter(key)));
            }
        };
    }

    public static Function<URL, String> TO_ADDRESS_FUNC = new Function<URL, String>() {
        @Override
        public String apply(URL input) {
            return input.getAddress();
        }
    };
}
