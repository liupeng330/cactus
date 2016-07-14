/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

/**
 * @author zhenyu.nie created on 2014 14-1-3 下午9:04
 */
public class EnableToDeleteException extends RuntimeException {
    public EnableToDeleteException() {
    }

    public EnableToDeleteException(String message) {
        super(message);
    }

    public EnableToDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnableToDeleteException(Throwable cause) {
        super(cause);
    }
}
