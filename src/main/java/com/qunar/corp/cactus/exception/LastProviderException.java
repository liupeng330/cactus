/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * @author zhenyu.nie created on 2013 13-11-18 上午4:21
 */
public class LastProviderException extends RuntimeException {
    private final ServiceSign serviceSign;

    public LastProviderException(ServiceSign serviceSign) {
        super();
        this.serviceSign = serviceSign;
    }

    public LastProviderException(ServiceSign serviceSign, String message) {
        super(message);
        this.serviceSign = serviceSign;
    }

    public LastProviderException(ServiceSign serviceSign, String message, Throwable cause) {
        super(message, cause);
        this.serviceSign = serviceSign;
    }

    public LastProviderException(ServiceSign serviceSign, Throwable cause) {
        super(cause);
        this.serviceSign = serviceSign;
    }

    public ServiceSign getServiceSign() {
        return serviceSign;
    }
}
