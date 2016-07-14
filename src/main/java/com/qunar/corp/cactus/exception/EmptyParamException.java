/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

/**
 * @author zhenyu.nie created on 2013 13-11-26 下午2:24
 */
public class EmptyParamException extends IllegalArgumentException {

    public EmptyParamException() {
        super();
    }

    public EmptyParamException(String message) {
        super(message);
    }

    public EmptyParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyParamException(Throwable cause) {
        super(cause);
    }
}
