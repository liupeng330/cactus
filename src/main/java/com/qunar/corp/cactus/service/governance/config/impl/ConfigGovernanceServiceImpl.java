package com.qunar.corp.cactus.service.governance.config.impl;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.checker.DynamicConfigChecker;
import com.qunar.corp.cactus.event.EventType;
import com.qunar.corp.cactus.service.governance.AbstractGovernanceService;
import com.qunar.corp.cactus.service.governance.IGovernanceService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.support.protocol.GovernanceFactory;
import com.qunar.corp.cactus.util.ConstantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.alibaba.dubbo.common.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-12-23 Time: 下午12:12
 */
@Service("configGovernanceService")
public class ConfigGovernanceServiceImpl extends AbstractGovernanceService implements IGovernanceService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigGovernanceServiceImpl.class);

    @Resource
    private DynamicConfigChecker dynamicConfigChecker;

    @Autowired
    public ConfigGovernanceServiceImpl(@Qualifier("configFactory") GovernanceFactory governanceFactory) {
        super(governanceFactory);
    }

    // 可能抛出异常 NullPointerException, IllegalKeyException, IllegalParamException,
    @Override
    protected Map<String, String> preprocess(List<ServiceSign> signs, Map<String, String> params) {
        Map<String, String> realParams = removeEmptyParams(params);
        dynamicConfigChecker.checkValues(realParams);
        return realParams;
    }

    @Override
    protected Result deal(GovernanceData data, List<GovernanceData> relativeDatas, Status status) {
        Result result = new Result();
        if (Objects.equal(data.getStatus(), status)) {
            return result;
        }

        result.insert(ConstantHelper.STATUS_EVENT_TYPE_MAP.get(status), data);
        List<GovernanceData> userDatas = FluentIterable.from(relativeDatas).filter(isUserData())
                .toSortedList(TIME_COMPARATOR);
        GovernanceData zkData = FluentIterable.from(relativeDatas).filter(isZkData()).first().orNull();
        List<GovernanceData> updatedUserDatas = getUpdatedUserDatas(data, userDatas, status);
        URL mergedUrl = getMergedUrl(updatedUserDatas);

        if (!urlEqual(mergedUrl, zkData)) {
            if (zkData != null && mergedUrl == null) {
                deleteZkData(zkData, result);
            } else if (zkData == null && mergedUrl != null) {
                insertZkData(updatedUserDatas, mergedUrl, result);
            } else {
                deleteZkData(zkData, result);
                insertZkData(updatedUserDatas, mergedUrl, result);
            }
        }
        return result;
    }

    private URL getMergedUrl(List<GovernanceData> userDatas) {
        URL mergedUrl = null;
        if (!userDatas.isEmpty()) {
            Iterator<GovernanceData> iterator = userDatas.iterator();
            mergedUrl = iterator.next().getUrl();
            while (iterator.hasNext()) {
                mergedUrl = mergedUrl.addParameters(iterator.next().getUrl().getParameters());
            }
        }
        return mergedUrl;
    }

    private List<GovernanceData> getUpdatedUserDatas(GovernanceData data, List<GovernanceData> userDatas, Status status) {
        if (data.getStatus() == Status.ENABLE && status != Status.ENABLE) {
            return FluentIterable.from(userDatas).filter(Predicates.not(hasId(data.getId()))).toList();
        } else if (data.getStatus() != Status.ENABLE && status == Status.ENABLE) {
            List<GovernanceData> datas = Lists.newArrayList(userDatas);
            datas.add(data);
            return datas;
        } else {
            return userDatas;
        }
    }

    @Override
    protected void check(GovernanceData data, List<GovernanceData> relativeDatas) {
        ServiceSign sign = data.getServiceSign();
        commonChecker.checkValidState(sign);
        if (!isConsumerData(data)) {
            dynamicConfigChecker.checkValidProvider(sign);
        }
    }

    private boolean isConsumerData(GovernanceData data) {
        return !isNullOrEmpty(data.getUrl().getParameter(MOCK_KEY));
    }

    @Override
    protected List<GovernanceData> getRelativeDatas(GovernanceData data) {
        return queryService
                .getGovernanceDatas(
                        GovernanceQuery.createBuilder().fromServiceSign(data.getServiceSign()).status(Status.ENABLE)
                                .pathType(PathType.CONFIG).build()).filter(getPredicates(data)).toList();
    }

    private boolean urlEqual(URL mergedUrl, GovernanceData data) {
        if (mergedUrl == null && data == null) {
            return true;
        } else if (mergedUrl != null && data != null) {
            return Objects.equal(mergedUrl, data.getUrl());
        } else {
            return mergedUrl != null && Objects.equal(MockEnum.NONEMOCK, mergedUrl.getParameter(MOCK_KEY));
        }
    }

    private void insertZkData(List<GovernanceData> userDatas, URL mergedUrl, Result result) {
        if (MockEnum.NONEMOCK.getText().equals(mergedUrl.getParameter(MOCK_KEY))) {
            return;
        }
        GovernanceData zkData = userDatas.get(0).copy();
        zkData.setLastOperator(UserContainer.getUserName());
        zkData.setName("");
        zkData.setDataType(DataType.ZKDATA);
        zkData.setId(null);
        zkData.setUrl(mergedUrl);
        result.addRegister(zkData);
        result.insert(EventType.NEW, zkData);
    }

    private void deleteZkData(GovernanceData zkData, Result result) {
        result.addUnRegister(zkData);
        result.insert(EventType.DELETE, zkData);
    }

    private Predicate<GovernanceData> getPredicates(GovernanceData data) {
        URL url = data.getUrl();
        Predicate<GovernanceData> dealDisabledKey = isNullOrEmpty(url.getParameter(DISABLED_KEY)) ? hasNoKeyOf(DISABLED_KEY)
                : hasKeyOf(DISABLED_KEY);
        Predicate<GovernanceData> dealAppKey = isNullOrEmpty(url.getParameter(APPLICATION_KEY)) ? hasNoKeyOf(APPLICATION_KEY)
                : matchApp(url.getParameter(APPLICATION_KEY));
        Predicate<GovernanceData> dealMock = isNullOrEmpty(url.getParameter(MOCK_KEY)) ? hasNoKeyOf(MOCK_KEY)
                : hasKeyOf(MOCK_KEY);
        Predicate<GovernanceData> dealLoadBalanceKey = isNullOrEmpty(url.getParameter(LOADBALANCE_KEY)) ? hasNoKeyOf(LOADBALANCE_KEY)
                : hasKeyOf(LOADBALANCE_KEY);
        return Predicates.and(FluentIterable.from(
                ImmutableList.of(dealDisabledKey, dealAppKey, dealMock, dealLoadBalanceKey)).filter(
                Predicates.notNull()));
    }

    private static final Comparator<GovernanceData> TIME_COMPARATOR = new Comparator<GovernanceData>() {
        @Override
        public int compare(GovernanceData lhs, GovernanceData rhs) {
            return lhs.getLastUpdateTime().compareTo(rhs.getLastUpdateTime());
        }
    };
}
