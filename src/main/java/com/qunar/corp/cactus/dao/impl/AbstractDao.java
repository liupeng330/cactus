package com.qunar.corp.cactus.dao.impl;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * Date: 13-10-28
 * Time: 下午5:07
 *
 * @author: xiao.liang
 * @description:
 */
public abstract class AbstractDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected SqlSession sqlSession;

    @Resource
    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
}
