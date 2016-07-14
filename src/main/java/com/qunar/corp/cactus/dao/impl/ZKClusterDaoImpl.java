package com.qunar.corp.cactus.dao.impl;

import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.ZKCluster;
import com.qunar.corp.cactus.dao.ZKClusterDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-10-28
 * Time: 下午6:57
 */
@Service
public class ZKClusterDaoImpl extends AbstractDao implements ZKClusterDao {

    private static final Map<String, Object> EMPTY_MAP = Maps.newHashMapWithExpectedSize(1);
    @Override
    public List<ZKCluster> findAllCluster() {
        return findByCondition(EMPTY_MAP);
    }

    @Override
    public List<ZKCluster> findByCondition(Map<String, Object> param) {
        return sqlSession.selectList("zkCluster.select", param);
    }

    @Override
    public ZKCluster insertCluster(ZKCluster zkCluster) {
        ZKCluster result = new ZKCluster(zkCluster);
        int rows = sqlSession.insert("zkCluster.insert", result);
        if (rows != 1) {
            throw new RuntimeException("insert failed");
        }
        return result;
    }
}
