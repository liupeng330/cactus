/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web.util;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.web.model.Consumer;
import com.qunar.corp.cactus.web.model.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static com.alibaba.dubbo.common.Constants.MOCK_KEY;
import static com.alibaba.dubbo.common.Constants.WEIGHT_KEY;

/**
 * @author zhenyu.nie created on 2013 13-11-22 下午8:19
 */
public class UrlConverter {

    private static final Logger logger = LoggerFactory.getLogger(UrlConverter.class);

    public static List<Provider> convertUrlsToProviders(String group, List<URL> urls) {
        return FluentIterable.from(urls).transform(new URLToProviderFunction(group)).toList();
    }

    public static List<Consumer> convertUrlsToConsumers(String group, List<URL> urls) {
        return FluentIterable.from(urls).transform(new URLToConsumerFunction(group)).toList();
    }

    public static class URLToConsumerFunction implements Function<URL, Consumer> {
        private final String group;

        public URLToConsumerFunction(String group) {
            this.group = group;
        }

        @Override
        public Consumer apply(URL input) {
            return getConsumerFromUrl(input, group);
        }
    }

    public static class URLToProviderFunction implements Function<URL, Provider> {

        private final String group;

        public URLToProviderFunction(String group) {
            this.group = group;
        }

        @Override
        public Provider apply(com.alibaba.dubbo.common.URL input) {
            return getProviderFromUrl(input, group);
        }
    }

    public static Consumer getConsumerFromUrl(URL url, String group) {
        Consumer consumer = new Consumer();
        consumer.setServiceKey(url.getServiceKey());
        consumer.setInterfaze(url.getParameter(Constants.INTERFACE_KEY));
        consumer.setServiceGroup(url.getParameter(Constants.GROUP_KEY, ""));
        consumer.setVersion(url.getParameter(Constants.VERSION_KEY, ""));
        consumer.setDubboVersion(url.getParameter(Constants.DUBBO_VERSION_KEY));
        consumer.setPid(url.getParameter(Constants.PID_KEY));
        consumer.setAddress(url.getAddress());
        consumer.setHostName(CommonCache.getHostNameByIp(url.getIp()));
        consumer.setApplication(url.getParameter(Constants.APPLICATION_KEY, ""));
        String urlStr = url.toFullString();
        consumer.setUrl(urlStr);
        consumer.setRealUrl(url);
        try {
            consumer.setEncodeUrl(URLEncoder.encode(urlStr, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("An exception accurred when encode consumer url!", e);
        }
        consumer.setMock((url.getParameter(MOCK_KEY, "")));
        consumer.setGroup(group);
        consumer.setParams(url.getParameters());
        return consumer;
    }

    public static Provider getProviderFromUrl(URL url, String group) {

        Provider provider = new Provider();
        provider.setServiceKey(url.getServiceKey());
        provider.setAddress(url.getAddress());
        provider.setHostNameAndPort(url.getPort() == 0 ? CommonCache.getHostNameByIp(url.getIp()) : CommonCache
                .getHostNameByIp(url.getIp()) + ":" + url.getPort());
        provider.setApplication(url.getParameter(Constants.APPLICATION_KEY));
        String urlStr = url.toFullString();
        provider.setUrl(urlStr);
        try {
            provider.setEncodeUrl(URLEncoder.encode(urlStr, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("An exception accurred when encode provider url!", e);
        }
        provider.setDynamic(url.getParameter(Constants.DYNAMIC_KEY, true));
        provider.setEnabled(url.getParameter(Constants.DISABLED_KEY, true));
        provider.setWeight(url.getParameter(Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT));
        provider.setUsername(url.getParameter("owner"));
        provider.setIntefaze(url.getParameter(Constants.INTERFACE_KEY));
        provider.setMethods(url.getParameter(Constants.METHODS_KEY));
        provider.setVersion(url.getParameter(Constants.VERSION_KEY));
        provider.setServiceGroup(url.getParameter(Constants.GROUP_KEY));
        provider.setDubboVersion(url.getParameter(Constants.DUBBO_VERSION_KEY));
        provider.setPid(url.getParameter(Constants.PID_KEY));
        provider.setGroup(group);
        provider.setParams(url.getParameters());
        return provider;
    }

    private static URL dealDefault(URL url) {
        Map<String, String> params = url.getParameters();
        Map<String, String> defaultParams = Maps.filterKeys(params, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.startsWith(Constants.DEFAULT_KEY_PREFIX)
                        && input.length() > Constants.DEFAULT_KEY_PREFIX.length();
            }
        });
        Map<String, String> paramsToAdd = Maps.newHashMap();
        for (Map.Entry<String, String> defaultEntry : defaultParams.entrySet()) {
            String key = defaultEntry.getKey().substring(Constants.DEFAULT_KEY_PREFIX.length());
            String defaultValue = defaultEntry.getValue();
            if (Strings.isNullOrEmpty(params.get(key)) && !Strings.isNullOrEmpty(defaultValue)) {
                paramsToAdd.put(key, defaultValue);
            }
        }
        return url.addParameters(paramsToAdd);
    }

    public static Function<URL, URL> providerUrlToShow() {
        return new Function<URL, URL>() {
            @Override
            public URL apply(URL input) {
                boolean enabled;
                if (input.hasParameter(Constants.DISABLED_KEY)) {
                    enabled = !input.getParameter(Constants.DISABLED_KEY, false);
                } else {
                    enabled = input.getParameter(Constants.ENABLED_KEY, true);
                }
                input = input.addParameter(Constants.DISABLED_KEY, String.valueOf(!enabled));
                if (Strings.isNullOrEmpty(input.getParameter(WEIGHT_KEY))) {
                    input = input.addParameter(WEIGHT_KEY, "100");
                }
                return dealDefault(input);
            }
        };
    }
}
