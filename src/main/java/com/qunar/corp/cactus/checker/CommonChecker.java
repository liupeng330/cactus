package com.qunar.corp.cactus.checker;

import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * @author zhenyu.nie created on 2013 13-11-29 上午3:43
 */
public interface CommonChecker {

    void checkValidState(ServiceSign sign);

    void checkDubboVersion(ServiceSign sign);
}
