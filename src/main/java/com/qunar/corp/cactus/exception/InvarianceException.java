/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

/**
 * @author zhenyu.nie created on 2013 13-11-11 下午2:15
 */
public class InvarianceException extends RuntimeException {
    public InvarianceException() {
        super();
    }

    public InvarianceException(String message) {
        super(message);
    }

    public InvarianceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvarianceException(Throwable cause) {
        super(cause);
    }
}
