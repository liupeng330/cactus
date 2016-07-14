package com.qunar.corp.cactus.support;

import com.qunar.corp.cactus.util.ConstantHelper;

import java.security.MessageDigest;

/**
 * @author zhenyu.nie created on 2013 13-12-11 下午7:56
 */

public class UserContainer {

    private static final ThreadLocal<Integer> userId = new ThreadLocal<Integer>();

    private static final ThreadLocal<String> userName = new ThreadLocal<String>();

    public static void setUserId(int id) {
        userId.set(id);
    }

    public static int getUserId() {
        Integer id = userId.get();
        if (id == null) {
            return ConstantHelper.UNLOGIN_UID;
        } else {
            return id;
        }
    }

    public static void setUserName(String name) {
        userName.set(name);
    }

    public static String getUserName() {
        return userName.get();
    }

    public static void remove() {
        userId.remove();
        userName.remove();
    }
}
