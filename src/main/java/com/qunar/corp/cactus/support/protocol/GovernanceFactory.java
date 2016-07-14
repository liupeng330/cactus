package com.qunar.corp.cactus.support.protocol;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/4 12:52
 */
public interface GovernanceFactory {

    GovernanceData create(ServiceSign sign, Map<String, String> params);
}
