package com.qunar.corp.cactus.service.governance.config;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.MockEnum;
import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 19:37
 */
public interface MockConfigService {

    void setMock(ServiceSign sign, String appName, MockEnum mock);

    void changeMock(GovernanceData oldData, MockEnum mock);
}
