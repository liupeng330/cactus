/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author zhenyu.nie created on 2013 13-11-11 下午4:56
 */
public enum MockEnum implements TextHolder {

    SHIELD("force:return+null"), FAILOVER("fail:return+null"), NONEMOCK("nonemock");

    private String text;

    MockEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static MockEnum makeMockEnum(String text) {
        text = Preconditions.checkNotNull(text).trim();
        if (Strings.isNullOrEmpty(text)) {
            return NONEMOCK;
        }
        for (MockEnum mockEnum : MockEnum.values()) {
            if (mockEnum.getText().equalsIgnoreCase(text.trim())) {
                return mockEnum;
            }
        }
        throw new IllegalArgumentException("invalid text to generate " + MockEnum.class.getName());
    }
}
