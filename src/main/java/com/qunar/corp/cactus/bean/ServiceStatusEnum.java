/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

/**
 * @author zhenyu.nie created on 2014 14-1-10 下午10:06
 */
public enum ServiceStatusEnum implements TextHolder {

    ONLINE("online"), OFFLINE("offline");

    private String text;

    private ServiceStatusEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
