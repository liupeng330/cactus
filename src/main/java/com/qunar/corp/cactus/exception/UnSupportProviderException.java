/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

import com.alibaba.dubbo.common.URL;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author zhenyu.nie created on 2013 13-11-29 上午3:57
 */
public class UnSupportProviderException extends RuntimeException {

    private final List<URL> urls = Lists.newArrayList();

    public UnSupportProviderException(List<URL> urls) {
        super();
        this.urls.addAll(urls);
    }

    public UnSupportProviderException(List<URL> urls, String message) {
        super(message);
        this.urls.addAll(urls);
    }

    public UnSupportProviderException(List<URL> urls, String message, Throwable cause) {
        super(message, cause);
        this.urls.addAll(urls);
    }

    public UnSupportProviderException(List<URL> urls, Throwable cause) {
        super(cause);
        this.urls.addAll(urls);
    }

    public List<URL> getUrls() {
        return urls;
    }
}
