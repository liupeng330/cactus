package com.qunar.corp.cactus.service.governance.config;

import com.google.common.base.Optional;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.checker.CommonChecker;
import com.qunar.corp.cactus.checker.DynamicConfigChecker;
import com.qunar.corp.cactus.dao.GovernanceDataDao;
import com.qunar.corp.cactus.service.AbstractService;
import com.qunar.corp.cactus.service.governance.IGovernanceService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 19:51
 */
public abstract class AbstractConfigService extends AbstractService {

    @Resource(name = "configGovernanceService")
    protected IGovernanceService configGovernanceService;

    @Resource
    protected CommonChecker commonChecker;

    @Resource
    protected GovernanceDataDao dao;

    @Resource
    protected DynamicConfigChecker dynamicConfigChecker;

    @Resource
    protected ProviderService providerService;

    @Resource
    protected ConsumerService consumerService;

    @Resource
    protected IQueryGovernanceService queryService;

    protected Optional<Long> addOneData(ServiceSign sign, Map<String, String> params) {
        List<Long> ids = configGovernanceService.addDatas(Arrays.asList(sign), params);
        if (ids.isEmpty()) {
            return Optional.absent();
        } else {
            return Optional.of(ids.get(0));
        }
    }
}
