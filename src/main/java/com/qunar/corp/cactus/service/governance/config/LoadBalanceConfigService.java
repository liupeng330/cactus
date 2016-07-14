package com.qunar.corp.cactus.service.governance.config;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 19:47
 */
public interface LoadBalanceConfigService {

    void changeLoadBalance(GovernanceData oldData, String loadBalance);

    void setLoadBalance(ServiceSign sign, String loadBalance);

    @SuppressWarnings("unchecked")
    Set<String> getAvailableLoadBalances(ServiceSign sign);

    @SuppressWarnings("unchecked")
    String getZkIdServiceKeyMatchedLoadBalance(ServiceSign sign);
}
