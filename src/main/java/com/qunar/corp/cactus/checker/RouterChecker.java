package com.qunar.corp.cactus.checker;

import java.util.List;
import java.util.Map;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * @author zhenyu.nie created on 2013 13-12-6 下午5:53
 */
public interface RouterChecker {

    // 可能的Exception: IllegalParamException
    void checkValidRouteParams(Map<String, String> params);

    void checkIllegalCondition(ServiceSign sign, Map<String, String> params);

    void checkIllegalCondition(GovernanceData data, List<GovernanceData> relativeDatas);
}
