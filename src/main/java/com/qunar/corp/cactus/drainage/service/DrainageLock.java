package com.qunar.corp.cactus.drainage.service;

import com.qunar.corp.cactus.drainage.bean.DrainageParam;

/**
 * @author sen.chai
 * @date 2015-04-22 22:18
 */
public interface DrainageLock {

    void lock(DrainageParam drainageParam);

    void unlock(DrainageParam drainageParam);
}
