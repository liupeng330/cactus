package com.qunar.corp.cactus.service.governance.config.impl;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.exception.EmptyParamException;
import com.qunar.corp.cactus.service.governance.config.AbstractConfigService;
import com.qunar.corp.cactus.service.governance.config.NormalConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 20:57
 */
@Service
public class NormalConfigServiceImpl extends AbstractConfigService implements NormalConfigService {

    private static final Logger logger = LoggerFactory.getLogger(NormalConfigServiceImpl.class);

    @Override
    public void addNormalConfig(List<ServiceSign> signs, Map<String, String> params) {
        logger.info("add normal config to service sign {} with params {}", signs, params);
        checkNonTrivialSigns(signs);
        Map<String, String> realParams = removeEmptyParams(params);
        checkNonTrivialParams(realParams);
        for (ServiceSign sign : signs) {
            dynamicConfigChecker.checkKeys(sign, realParams);
        }
        configGovernanceService.addDatas(signs, realParams);
    }

    @Override
    public void changeNormalConfig(GovernanceData data, Map<String, String> params) {
        logger.info("change normal config to data {} with params {}", data, params);
        Map<String, String> realParams = removeEmptyParams(params);
        checkNonTrivialParams(realParams);
        dynamicConfigChecker.checkKeys(data.getServiceSign(), realParams);
        configGovernanceService.changeDatas(data, realParams);
    }

    private void checkNonTrivialSigns(List<ServiceSign> signs) {
        if (signs == null || signs.isEmpty()) {
            logger.warn("no sign is picked");
            throw new EmptyParamException("no sign is picked");
        }
    }

    private void checkNonTrivialParams(Map<String, String> realParams) {
        if (isTrivialParams(realParams)) {
            logger.warn("no param need to be deal");
            throw new EmptyParamException("no param need to be deal");
        }
    }
}
