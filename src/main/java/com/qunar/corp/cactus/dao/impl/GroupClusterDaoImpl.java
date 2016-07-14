package com.qunar.corp.cactus.dao.impl;

import com.qunar.corp.cactus.dao.GroupClusterDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhenyu.nie created on 2014 2014/12/23 18:13
 */
@Repository
public class GroupClusterDaoImpl extends AbstractDao implements GroupClusterDao {

    @Override
    public List<String> selectAll() {
        return sqlSession.selectList("groupCluster.selectAll");
    }

    @Override
    public int insert(String cluster) {
        return sqlSession.insert("groupCluster.insert", cluster);
    }

    @Override
    public int delete(String cluster) {
        return sqlSession.delete("groupCluster.delete", cluster);
    }
}
