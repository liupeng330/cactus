package com.qunar.corp.cactus.service;

import com.qunar.corp.cactus.exception.GroupClusterExistException;

import java.util.List;

/**
 * @author zhenyu.nie created on 2014 14-3-18 下午9:44
 *
 */
public interface SpecialService {

    void freshSpecials();

    void add(String group) throws GroupClusterExistException;

    List<String> getGroups();

    List<String> getGroupClusters();

    int delete(String cluster);

}
