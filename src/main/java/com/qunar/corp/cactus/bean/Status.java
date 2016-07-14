/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午2:05
 */
public enum Status implements TextHolder {

    ENABLE("enable", 0),
    DISABLE("disable", 1),
    DEL("delete", 2);

    public final String text;
    public final int code;

    Status(String text, int code) {
        this.text = text;
        this.code = code;
    }

    public static Status fromCode(int code) {
        for (Status status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("no code found in status");
    }

    public static Status fromText(String text) {
        text = Preconditions.checkNotNull(text);
        for (Status status : values()) {
            if (Objects.equal(text, status.text)) {
                return status;
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
