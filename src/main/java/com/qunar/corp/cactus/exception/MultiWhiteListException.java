/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

/**
 * @author zhenyu.nie created on 2013 13-12-13 上午1:43
 */
public class MultiWhiteListException extends InvarianceException {
    public MultiWhiteListException() {
    }

    public MultiWhiteListException(String message) {
        super(message);
    }

    public MultiWhiteListException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiWhiteListException(Throwable cause) {
        super(cause);
    }
}
