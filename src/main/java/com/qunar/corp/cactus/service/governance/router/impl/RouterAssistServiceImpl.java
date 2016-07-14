package com.qunar.corp.cactus.service.governance.router.impl;

import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.checker.RouterChecker;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.AbstractService;
import com.qunar.corp.cactus.service.governance.router.RouterAssistService;
import com.qunar.corp.cactus.service.governance.router.RouterConstants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 18:50
 */
@Service
public class RouterAssistServiceImpl extends AbstractService implements RouterAssistService {

    @Resource
    private RouterChecker routerChecker;

    @Resource
    private IQueryGovernanceService queryService;

    @Override
    public Map<String, String> checkAndExtractValidParams(Map<String, String> params) {
        Map<String, String> nonTrivialParams = filterTrivialParams(removeEmptyParams(params));
        routerChecker.checkValidRouteParams(nonTrivialParams);
        return nonTrivialParams;
    }

    @Override
    public List<GovernanceData> getRelativeDatas(ServiceSign sign) {
        return queryService.getGovernanceDatas(
                GovernanceQuery.createBuilder().zkId(sign.getZkId()).group(sign.getGroup())
                        .serviceName(sign.getServiceInterface()).status(Status.ENABLE).pathType(PathType.ROUTER)
                        .dataType(DataType.ZKDATA).build()).toList();
    }

    private Map<String, String> filterTrivialParams(Map<String, String> params) {
        Map<String, String> nonTrivialParams = Maps.newHashMap();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (RouterConstants.KEYS.contains(key)) {
                nonTrivialParams.put(key, entry.getValue());
            }
        }
        return nonTrivialParams;
    }
}
