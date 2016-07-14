package com.qunar.corp.cactus.web.model;

import com.qunar.corp.cactus.util.Pair;

import java.util.Map;
import java.util.Set;

/**
 * Date: 13-11-10 Time: 下午5:51
 * 
 * @author: xiao.liang
 * @description:
 */
public class GroupRelationship {

    private String group;
    private Set<String> relyOn;
    private Pair<Set<String>, Map<String, Set<String>>> relyMe;

    public GroupRelationship(String group, Set<String> relyOn, Pair<Set<String>, Map<String, Set<String>>> relyMe) {
        this.group = group;
        this.relyOn = relyOn;
        this.relyMe = relyMe;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Set<String> getRelyOn() {
        return relyOn;
    }

    public void setRelyOn(Set<String> relyOn) {
        this.relyOn = relyOn;
    }

    public Pair<Set<String>, Map<String, Set<String>>> getRelyMe() {
        return relyMe;
    }

    public void setRelyMe(Pair<Set<String>, Map<String, Set<String>>> relyMe) {
        this.relyMe = relyMe;
    }
}
