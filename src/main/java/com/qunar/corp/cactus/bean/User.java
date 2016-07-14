package com.qunar.corp.cactus.bean;

/**
 * Date: 13-11-6
 * Time: 下午5:50
 *
 * @author: xiao.liang
 * @description:
 */
public class User {

    private int id;
    private String username;
    private Role role;   //这个role除了管理员权限，其他权限无意义，要判断是否为owner需要实时从ownerCache缓存中判断

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
