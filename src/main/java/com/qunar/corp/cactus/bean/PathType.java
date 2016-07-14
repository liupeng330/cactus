/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.alibaba.dubbo.common.Constants;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午2:03
 */
public enum PathType implements TextHolder {

    PROVIDER(Constants.PROVIDER_PROTOCOL, 1),
    CONSUMER(Constants.CONSUMER_PROTOCOL, 2),
    ROUTER(Constants.ROUTE_PROTOCOL, 3),
    CONFIG(Constants.OVERRIDE_PROTOCOL, 4);

    public final String text;
    public final int code;

    PathType(String providerProtocol, int code) {
        this.text = providerProtocol;
        this.code = code;
    }

    public static PathType fromCode(int code) {
        for (PathType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("no code found in PathType");
    }

    public static PathType fromText(String text) {
        text = Preconditions.checkNotNull(text);
        for (PathType type : values()) {
            if (Objects.equal(text, type.text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("no text found in PathType");
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }
}
