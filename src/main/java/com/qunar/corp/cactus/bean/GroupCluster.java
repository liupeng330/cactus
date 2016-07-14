package com.qunar.corp.cactus.bean;

/**
 * Date: 14-8-11
 * Time: 上午10:55
 *
 * @author: xiao.liang
 * @description:
 */
public class GroupCluster {

    private int id;
    private String group;

    public GroupCluster() {
    }

    public GroupCluster(String group) {
        this.group = group;
    }

    public GroupCluster(int id, String group) {
        this.id = id;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
