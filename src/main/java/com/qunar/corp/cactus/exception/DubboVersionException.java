/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

import com.alibaba.dubbo.common.URL;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * @author zhenyu.nie created on 2013 13-11-28 下午12:21
 */
public class DubboVersionException extends RuntimeException {

    private final String leastVersion;

    private final List<URL> urls;

    public DubboVersionException(String leastVersion, List<URL> urls) {
        super();
        this.urls = Lists.newArrayList(urls);
        this.leastVersion = leastVersion;
    }

    public DubboVersionException(String leastVersion, List<URL> urls, String message) {
        super(message);
        this.urls = Lists.newArrayList(urls);
        this.leastVersion = leastVersion;
    }

    public DubboVersionException(String leastVersion, List<URL> urls, String message, Throwable cause) {
        super(message, cause);
        this.urls = Lists.newArrayList(urls);
        this.leastVersion = leastVersion;
    }

    public DubboVersionException(String leastVersion, List<URL> urls, Throwable cause) {
        super(cause);
        this.urls = Lists.newArrayList(urls);
        this.leastVersion = leastVersion;
    }

    public List<URL> getUrls() {
        return Collections.unmodifiableList(urls);
    }

    public String getLeastVersion() {
        return leastVersion;
    }
}
