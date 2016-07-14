package com.qunar.corp.cactus.service.impl;

import com.qunar.corp.cactus.bean.Log;
import com.qunar.corp.cactus.bean.LogQuery;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.bean.User;
import com.qunar.corp.cactus.dao.LogDao;
import com.qunar.corp.cactus.service.LogService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.ConstantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Date: 13-11-8 Time: 上午11:45
 * 
 * @author: xiao.liang
 * @description:
 */
@Service
public class LogServiceImpl implements LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Resource
    private LogDao logDao;

    @Override
    public int add(Log log) {
        try {
            return logDao.add(log);
        } catch (Throwable e) {
            logger.error("occur error record log: {}{}", ConstantHelper.NEW_LINE, log, e);
            return 0;
        }
    }

    @Override
    public int add(ServiceSign serviceSign, String msg) {
        int uid = UserContainer.getUserId();
        if (uid == ConstantHelper.INVALID_UID || uid == ConstantHelper.UNLOGIN_UID) {
            return 0;
        }
        Log log = new Log();
        User user = new User();
        user.setId(uid);
        log.setUser(user);
        log.setGroup(serviceSign.getGroup());
        log.setService(serviceSign.getServiceKey());
        log.setMessage(msg);
        log.setHostName(CommonCache.address2HostNameAndPort(serviceSign.getAddress()) + "("
                + serviceSign.getAddress() + ")");
        return add(log);
    }

    @Override
    public List<Log> listByUserName(String username, int fromIndex, int limit) {
        return search(LogQuery.makeBuilder().username(username).build(), fromIndex, limit);
    }

    @Override
    public List<Log> search(LogQuery logQuery, int fromIndex, int limit) {
        return logDao.list(logQuery, fromIndex, limit);
    }

    @Override
    public int getTotalSize(LogQuery logQuery) {
        return logDao.selectCount(logQuery);
    }
}
