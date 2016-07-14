/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2013 13-11-20 下午1:38
 */
public class IllegalParamException extends IllegalArgumentException {

    private final Map<String, String> illegalParams = Maps.newHashMap();

    public IllegalParamException(String key, String value) {
        super();
        this.illegalParams.put(key, value);
    }

    public IllegalParamException(String key, String value, String message) {
        super(message);
        this.illegalParams.put(key, value);
    }

    public IllegalParamException(Map<String, String> illegalParams) {
        super();
        this.illegalParams.putAll(illegalParams);
    }

    public IllegalParamException(Map<String, String> illegalParams, String message) {
        super(message);
        this.illegalParams.putAll(illegalParams);
    }

    public IllegalParamException(Map<String, String> illegalParams, String message, Throwable cause) {
        super(message, cause);
        this.illegalParams.putAll(illegalParams);
    }

    public IllegalParamException(Map<String, String> illegalParams, Throwable cause) {
        super(cause);
        this.illegalParams.putAll(illegalParams);
    }

    public Map<String, String> getIllegalParams() {
        return ImmutableMap.copyOf(illegalParams);
    }
}
