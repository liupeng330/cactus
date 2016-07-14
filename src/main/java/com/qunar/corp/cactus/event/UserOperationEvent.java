package com.qunar.corp.cactus.event;

import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * @author zhenyu.nie created on 2014 2014/12/23 20:33
 */
public class UserOperationEvent {

    private long uid;

    private ServiceSign sign;

    private String message;

    public UserOperationEvent(long uid, ServiceSign sign, String message) {
        this.uid = uid;
        this.sign = sign;
        this.message = message;
    }

    public long getUid() {
        return uid;
    }

    public ServiceSign getSign() {
        return sign;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "UserOperationEvent{" +
                "uid=" + uid +
                ", sign=" + sign +
                ", message='" + message + '\'' +
                '}';
    }
}
