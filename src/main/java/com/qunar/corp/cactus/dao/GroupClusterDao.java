package com.qunar.corp.cactus.dao;

import java.util.List;

/**
 * @author zhenyu.nie created on 2014 2014/12/23 14:44
 */
public interface GroupClusterDao {

    List<String> selectAll();

    int insert(String cluster);

    int delete(String cluster);
}
