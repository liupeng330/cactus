package com.qunar.corp.cactus.service.governance.router.impl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.checker.RouterChecker;
import com.qunar.corp.cactus.event.EventType;
import com.qunar.corp.cactus.service.governance.AbstractGovernanceService;
import com.qunar.corp.cactus.service.governance.IGovernanceService;
import com.qunar.corp.cactus.service.governance.router.RouterAssistService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.support.protocol.GovernanceFactory;
import com.qunar.corp.cactus.util.ConstantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-12-23 Time: 下午12:13
 */
@Service("routerGovernanceService")
public class RouterGovernanceServiceImpl extends AbstractGovernanceService implements IGovernanceService {

    private static final Logger logger = LoggerFactory.getLogger(RouterGovernanceServiceImpl.class);

    @Resource
    private RouterChecker routerChecker;

    @Resource
    private RouterAssistService routerAssistService;

    @Autowired
    public RouterGovernanceServiceImpl(@Qualifier("routerFactory") GovernanceFactory governanceFactory) {
        super(governanceFactory);
    }

    @Override
    protected Map<String, String> preprocess(List<ServiceSign> signs, Map<String, String> params) {
        return routerAssistService.checkAndExtractValidParams(params);
    }

    @Override
    protected void checkAddDatas(List<ServiceSign> signs, Map<String, String> params) {
        if (!signs.isEmpty()) {
            commonChecker.checkDubboVersion(signs.get(0));
        }
    }

    @Override
    protected void checkEnable(GovernanceData data) {
        commonChecker.checkDubboVersion(data.getServiceSign());
    }

    @Override
    protected Result deal(GovernanceData data, List<GovernanceData> relativeDatas, Status resultStatus) {
        Result result = new Result();
        if (Objects.equal(data.getStatus(), resultStatus)) {
            return result;
        }

        result.insert(ConstantHelper.STATUS_EVENT_TYPE_MAP.get(resultStatus), data);

        if (data.getStatus() == Status.ENABLE) {
            GovernanceData zkData = getCorrespondingZkData(data);
            if (zkData == null || zkData.getStatus() != Status.ENABLE) {
                makeErrorInfo(data, zkData, resultStatus);
            }
            deleteZkData(zkData, result);
        } else if (resultStatus == Status.ENABLE) {
            GovernanceData zkData = getCorrespondingZkData(data);
            if (zkData != null && zkData.getStatus() == Status.ENABLE) {
                makeErrorInfo(data, zkData, resultStatus);
            } else if (zkData == null) {
                insertZkData(makeZkData(data), result);
            } else {
                enableZkData(makeZkData(data).setId(zkData.getId()), result);
            }
        }
        return result;
    }

    private void makeErrorInfo(GovernanceData data, GovernanceData zkData, Status resultStatus) {
        logger.error("change data ({}) to status {} with corresponding zk data ({})", data, resultStatus, zkData);
        throw new IllegalStateException("change data (" + data + ") to status " + resultStatus
                + " with corresponding zk data (" + zkData + ")");
    }

    private GovernanceData makeZkData(GovernanceData data) {
        Preconditions.checkArgument(data.getId() != null && data.getId() > 0, "data should be db data");
        return data.copy().setDataType(DataType.ZKDATA).setId(null)
                .setUrl(data.getUrl().addParameter(ConstantHelper.USER_DATA_ID, String.valueOf(data.getId())));
    }

    private GovernanceData getCorrespondingZkData(GovernanceData data) {
        List<GovernanceData> governanceDatas = queryService
                .getGovernanceDatas(
                        GovernanceQuery.createBuilder().fromServiceSign(data.getServiceSign()).dataType(DataType.ZKDATA)
                                .build())
                .filter(hasKeyValue(ConstantHelper.USER_DATA_ID, String.valueOf(data.getId()))).toList();

        if (governanceDatas.isEmpty()) {
            return null;
        } else if (governanceDatas.size() > 1) {
            logger.error("data ({}) has too many zk data", data);
            throw new IllegalStateException("data (" + data + ") has too many zk data");
        } else {
            return governanceDatas.get(0);
        }
    }

    private void insertZkData(GovernanceData zkData, Result result) {
        zkData.setLastOperator(UserContainer.getUserName());
        zkData.setStatus(Status.ENABLE);
        result.insert(EventType.NEW, zkData);
        result.addRegister(zkData);
    }

    private void deleteZkData(GovernanceData zkData, Result result) {
        result.insert(EventType.DELETE, zkData);
        result.addUnRegister(zkData);
    }

    private void enableZkData(GovernanceData zkData, Result result) {
        result.insert(EventType.ENABLE, zkData);
        zkData.setLastOperator(UserContainer.getUserName());
        zkData.setStatus(Status.ENABLE);
        result.addRegister(zkData);
    }

    @Override
    protected void check(GovernanceData data, List<GovernanceData> relativeDatas) {
        routerChecker.checkIllegalCondition(data, relativeDatas);
    }

    @Override
    protected List<GovernanceData> getRelativeDatas(GovernanceData data) {
        return routerAssistService.getRelativeDatas(data.getServiceSign());
    }
}
