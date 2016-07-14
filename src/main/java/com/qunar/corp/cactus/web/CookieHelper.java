/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.util.ConstantHelper;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.qunar.corp.cactus.util.ConstantHelper.UID_COOKIE_NAME;
import static com.qunar.corp.cactus.util.ConstantHelper.USERNAME_COOKIE_NAME;

/**
 * @author zhenyu.nie created on 2013 13-12-27 下午4:39
 */
public class CookieHelper {

    private static final int MAX_AGE = 60 * 60 * 24;
    private static final String COOKIE_PATH = "/";

    // todo: 再生成一个cookie，防止伪造的cookie数据
    public static void encodeAndWriteUserInfoCookie(HttpServletResponse response, String username, String uid) {
        if (Strings.isNullOrEmpty(username)) {
            removeUserInfoCookie(response);
            return;
        }
        Cookie usernameCookie = new Cookie(USERNAME_COOKIE_NAME, encodeByBase64(username));
        Cookie uidCookie = new Cookie(UID_COOKIE_NAME, encodeByBase64(uid));
        usernameCookie.setMaxAge(MAX_AGE);
        usernameCookie.setPath(COOKIE_PATH);
        uidCookie.setMaxAge(MAX_AGE);
        uidCookie.setPath(COOKIE_PATH);
        response.addCookie(usernameCookie);
        response.addCookie(uidCookie);
    }

    public static void removeUserInfoCookie(HttpServletResponse response) {
        Cookie usernameCookie = new Cookie(USERNAME_COOKIE_NAME, null);
        Cookie uidCookie = new Cookie(UID_COOKIE_NAME, null);
        usernameCookie.setMaxAge(0);
        usernameCookie.setPath(COOKIE_PATH);
        uidCookie.setMaxAge(0);
        uidCookie.setPath(COOKIE_PATH);
        response.addCookie(usernameCookie);
        response.addCookie(uidCookie);
    }

    public static String getUsername(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (USERNAME_COOKIE_NAME.equals(cookie.getName())) {
                    return decodeByBase64(cookie.getValue());
                }
            }
        }
        return "";
    }

    public static Map<String, String> getDecodeUserInfoCookie(HttpServletRequest request) {
        Map<String, String> cookieParam = Maps.newHashMapWithExpectedSize(2);
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return cookieParam;
        }
        for (Cookie cookie : cookies) {
            if (USERNAME_COOKIE_NAME.equals(cookie.getName())) {
                cookieParam.put(ConstantHelper.USERNAME_COOKIE_NAME, decodeByBase64(cookie.getValue()));
            }
            if (UID_COOKIE_NAME.equals(cookie.getName())) {
                cookieParam.put(ConstantHelper.UID_COOKIE_NAME, decodeByBase64(cookie.getValue()));
            }
        }
        return cookieParam;
    }

    private static String encodeByBase64(String str) {
        if (Strings.isNullOrEmpty(str))
            return str;
        return new BASE64Encoder().encode(str.getBytes());
    }

    private static String decodeByBase64(String str) {
        if (Strings.isNullOrEmpty(str))
            return str;
        try {
            return new String(new BASE64Decoder().decodeBuffer(str));
        } catch (IOException e) {
            throw new RuntimeException("decode (" + str + ") failed when using base64", e);
        }
    }
}
