/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.dao.impl;

import com.qunar.corp.cactus.dao.SpecialDao;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author zhenyu.nie created on 2014 14-3-20 下午12:04
 */
@Repository
public class SpecialDaoImpl extends AbstractDao implements SpecialDao {

    @Override
    public List<String> selectAll() {
        return sqlSession.selectList("specialGroup.selectAll");
    }

    @Override
    public int clear(List<String> list) {
        return sqlSession.delete("specialGroup.clear", list);
    }

    @Override
    public int insert(List<String> list) {
        return sqlSession.insert("specialGroup.insert", list);
    }
}
