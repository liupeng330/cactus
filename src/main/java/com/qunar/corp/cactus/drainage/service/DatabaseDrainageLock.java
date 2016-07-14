package com.qunar.corp.cactus.drainage.service;

import com.google.common.base.Throwables;
import com.qunar.corp.cactus.drainage.bean.DrainageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sen.chai
 * @date 2015-04-23 13:58
 */
@Service
public class DatabaseDrainageLock implements DrainageLock {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDrainageLock.class);


    @Resource
    private DrainageInfoDao drainageInfoDao;

    @Override
    public void lock(DrainageParam drainageParam) {
        try {
            drainageInfoDao.insert(drainageParam);
        } catch (DuplicateKeyException ex) {
            logger.error("lock failed, {}", drainageParam, ex);
            throw new RuntimeException("该服务正在引流!");
        } catch (Exception e) {
            logger.error("lock failed, {}", drainageParam, e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void unlock(DrainageParam drainageParam) {
        drainageInfoDao.delete(drainageParam);
    }
}
