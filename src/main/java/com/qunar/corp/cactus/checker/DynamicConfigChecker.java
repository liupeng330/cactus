package com.qunar.corp.cactus.checker;

import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2013 13-12-9 下午10:21
 */
public interface DynamicConfigChecker {

    void checkArguments(ServiceSign serviceSign, Map<String, String> params);

    void checkValidProvider(ServiceSign sign);

//    void checkNotLastProvider(ServiceSign providerSign, List<GovernanceData> zkDatas);

    void checkNotLastProvider(ServiceSign providerSign);

    void checkKeys(ServiceSign serviceSign, Map<String, String> params);

    void checkValues(Map<String, String> params);
}
