package com.qunar.corp.cactus.service.governance.config.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.MockEnum;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.exception.IllegalParamException;
import com.qunar.corp.cactus.service.governance.config.AbstractConfigService;
import com.qunar.corp.cactus.service.governance.config.MockConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.alibaba.dubbo.common.Constants.APPLICATION_KEY;
import static com.alibaba.dubbo.common.Constants.MOCK_KEY;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.qunar.corp.cactus.util.ConstantHelper.NAME;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 19:38
 */
@Service
public class MockConfigServiceImpl extends AbstractConfigService implements MockConfigService {

    private static final Logger logger = LoggerFactory.getLogger(MockConfigServiceImpl.class);

    @Override
    @Transactional
    public void setMock(ServiceSign sign, String appName, MockEnum mock) {
        logger.info("set mock to service sign {} and app {} with {}", sign, appName, mock.getText());
        appName = nullToEmpty(appName).trim();
        checkPort(sign);
        checkAppName(appName);
        commonChecker.checkValidState(sign);
        Map<String, String> params = Maps.newHashMap();
        params.put(NAME, MOCK_KEY);
        params.put(MOCK_KEY, mock.getText());
        params.put(APPLICATION_KEY, appName);

        Optional<Long> id = addOneData(sign, params);
        if (!id.isPresent()) {
            throw new RuntimeException("add data but fail with " + sign + ", app name " + appName + ", " + mock.getText());
        }
        configGovernanceService.enable(dao.findById(id.get()));
    }

    @Override
    public void changeMock(GovernanceData oldData, MockEnum mock) {
        logger.info("change mock to {} with old data {}", mock, oldData);
        checkMockData(oldData);

        if (MockEnum.makeMockEnum(oldData.getUrl().getParameter(MOCK_KEY)) == mock) {
            logger.info("same mock with data {} and change mock {}", oldData, mock);
            return;
        }

        if (mock == MockEnum.NONEMOCK) {
            configGovernanceService.delete(oldData);
        } else {
            Map<String, String> params = Maps.newHashMap();
            params.put(MOCK_KEY, mock.getText());
            params.put(APPLICATION_KEY, oldData.getUrl().getParameter(APPLICATION_KEY));
            configGovernanceService.changeDatas(oldData, params);
        }
    }

    private void checkMockData(GovernanceData oldData) {
        if (isNullOrEmpty(oldData.getUrl().getParameter(MOCK_KEY))) {
            logger.error("unexpected mock data ({})", oldData);
            throw new IllegalArgumentException("unexpected mock data (" + oldData + ")");
        }
    }

    private void checkPort(ServiceSign sign) {
        if (sign.getPort() != 0) {
            logger.info("not consumer");
            throw new IllegalParamException("port", String.valueOf(sign.getPort()), "not consumer");
        }
    }

    private void checkAppName(String appName) {
        if (isNullOrEmpty(appName)) {
            logger.info("empty appName");
            throw new IllegalParamException(APPLICATION_KEY, appName, "empty appName");
        }
    }
}
