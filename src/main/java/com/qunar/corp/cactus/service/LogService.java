package com.qunar.corp.cactus.service;

import com.qunar.corp.cactus.bean.Log;
import com.qunar.corp.cactus.bean.LogQuery;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.List;

/**
 * Date: 13-11-8 Time: 上午11:23
 * 
 * @author: xiao.liang
 * @description:
 */
public interface LogService {

    int add(Log log);

    int add(ServiceSign serviceSign, String msg);

    List<Log> listByUserName(String username, int fromIndex, int limit);

    List<Log> search(LogQuery logQuery, int fromIndex, int limit);

    int getTotalSize(LogQuery logQuery);
}
