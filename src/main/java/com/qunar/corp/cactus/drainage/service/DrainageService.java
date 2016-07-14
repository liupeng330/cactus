package com.qunar.corp.cactus.drainage.service;

import com.qunar.corp.cactus.drainage.bean.DrainageParam;

/**
 * @author sen.chai
 * @date 2015-04-21 14:17
 */
public interface DrainageService {

    void start(DrainageParam param);

    void stop(DrainageParam param);

}
