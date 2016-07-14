/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.dao;

import java.util.Collection;
import java.util.List;

/**
 * @author zhenyu.nie created on 2014 14-3-20 下午12:02
 */
public interface SpecialDao {

    List<String> selectAll();

    int clear(List<String> groups);

    int insert(List<String> groups);
}
