package com.qunar.corp.cactus.service.governance.router;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/19 11:27
 *
 * 这个类用于对进行RouterGovernanceService和Router操作类的公共操作
 */
public interface RouterAssistService {

    Map<String, String> checkAndExtractValidParams(Map<String, String> params);

    List<GovernanceData> getRelativeDatas(ServiceSign sign);
}
