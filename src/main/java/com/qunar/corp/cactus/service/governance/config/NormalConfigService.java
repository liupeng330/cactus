package com.qunar.corp.cactus.service.governance.config;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 20:56
 */
public interface NormalConfigService {

    void addNormalConfig(List<ServiceSign> signs, Map<String, String> params);

    void changeNormalConfig(GovernanceData data, Map<String, String> params);
}
