/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

/**
 * @author zhenyu.nie created on 2013 13-11-29 下午6:39
 */
public class NoMatchedException extends IllegalArgumentException {
    public NoMatchedException() {
        super();
    }

    public NoMatchedException(String message) {
        super(message);
    }

    public NoMatchedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchedException(Throwable cause) {
        super(cause);
    }
}
