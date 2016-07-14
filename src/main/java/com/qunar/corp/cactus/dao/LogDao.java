package com.qunar.corp.cactus.dao;

import com.qunar.corp.cactus.bean.Log;

import java.util.List;
import java.util.Map;

/**
 * Date: 13-11-8
 * Time: 上午11:11
 *
 * @author: xiao.liang
 * @description:
 */
public interface LogDao {

    int add(Log log);

    List<Log> list(Map<String, Object> param, int fromIndex, int limit);

    int selectCount(Map<String,Object> param);

    int addCactusApiLog(Log log);
}
