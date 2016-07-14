/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.util;

import com.google.common.base.Strings;

/**
 * @author zhenyu.nie created on 2014 14-3-19 下午5:13
 */
public class PathUtil {

    public static String makePath(String... inputs) {
        StringBuilder sb = new StringBuilder();
        for (String input : inputs) {
            if (Strings.isNullOrEmpty(input)) {
                continue;
            } else if (input.startsWith("/")) {
                sb.append(input);
            } else {
                sb.append("/").append(input);
            }
        }

        if (sb.charAt(0) == '/') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    public static String getCluster(String path) {
        path = path.charAt(0) == '/' ? path.substring(1) : path;
        int endIndex = path.indexOf('/');
        if (endIndex == -1) {
            return path;
        } else {
            return path.substring(0, endIndex);
        }
    }

    public static String getGroup(String path) {
        return path.charAt(0) == '/' ? path.substring(1) : path;
    }

    public static String getMidPath(String prePath) {
        int index = prePath.indexOf('/', 1);
        if (index == -1) {
            return "";
        } else {
            return prePath.substring(index + 1);
        }
    }

    public static String ensureStartWithSlash(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    public static boolean isLegalService(String path) {
        return !Strings.isNullOrEmpty(path) && (path.startsWith("com.") || path.startsWith("qunar."));
    }
}
