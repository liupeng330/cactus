package com.qunar.corp.cactus.dao.impl;

import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.Log;
import com.qunar.corp.cactus.dao.LogDao;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Date: 13-11-8 Time: 上午11:13
 * 
 * @author: xiao.liang
 * @description:
 */
@Repository
public class LogDaoImpl extends AbstractDao implements LogDao {

    @Override
    public int add(Log log) {
        return sqlSession.insert("log.insert", log);
    }

    @Override
    public List<Log> list(Map<String, Object> param, int fromIndex, int limit) {
        return sqlSession.selectList("log.select", param, new RowBounds(fromIndex, limit));
    }

    @Override
    public int selectCount(Map<String, Object> param) {
        return (Integer)sqlSession.selectOne("log.selectCount", param);
    }

    @Override
    public int addCactusApiLog(Log log) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("zkId", log.getZkCluster().getId());
        param.put("group", log.getGroup());
        param.put("serviceGroup", log.getServiceGroup());
        param.put("service", log.getService());
        param.put("version", log.getVersion());
        param.put("ip", log.getIp());
        param.put("port", log.getPort());
        int count = selectCount(param);
        if (count > 0) {
            return sqlSession.update("log.updateCactusApiLog", log);
        } else {
            return add(log);
        }
    }
}
