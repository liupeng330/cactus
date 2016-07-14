/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.google.common.base.Objects;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午2:06
 */
public enum DataType implements TextHolder {

    USERDATA("userData", 0), ZKDATA("zkData", 1), APIDATA("apiData", 2);

    public final int code;
    public final String text;

    DataType(String text, int code) {
        this.text = text;
        this.code = code;
    }

    public static DataType fromCode(int code) {
        for (DataType dataType : values()) {
            if (dataType.code == code) {
                return dataType;
            }
        }
        throw new IllegalArgumentException("no code found in DataType");
    }

    public static DataType fromText(String text) {
        for (DataType dataType : values()) {
            if (Objects.equal(text, dataType.text)) {
                return dataType;
            }
        }
        throw new IllegalArgumentException("no text found in DataType");
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
