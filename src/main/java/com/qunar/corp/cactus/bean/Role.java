
package com.qunar.corp.cactus.bean;

/**
 * Date: 13-11-6
 * Time: 下午5:51
 *
 * @author: xiao.liang
 * @description:
 */
public enum Role {

    USER(0, "普通用户"), OWNER(1, "owner"), ADMIN(2, "超级管理员");

    private int code;

    public String getText() {
        return text;
    }

    private String text;

    private Role(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int code() {
        return code;
    }

    public int getCode() {
        return code;
    }

    public String text() {
        return text;
    }

    public static Role codeOf(int code) {
        for (Role role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role code: " + code);
    }
}
