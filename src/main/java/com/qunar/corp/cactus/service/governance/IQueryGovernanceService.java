package com.qunar.corp.cactus.service.governance;

import com.google.common.collect.FluentIterable;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.GovernanceQuery;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-12-23 Time: 下午12:17 查询治理url的接口
 */
public interface IQueryGovernanceService {

    GovernanceData getGovernanceDataById(long id);

    FluentIterable<GovernanceData> getServiceKeyMatchedGovernanceDatas(GovernanceQuery governanceQuery);

    FluentIterable<GovernanceData> getGovernanceDatas(GovernanceQuery governanceQuery);
}
