package com.qunar.corp.cactus.service.governance.config.impl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.exception.IllegalParamException;
import com.qunar.corp.cactus.service.governance.config.AbstractConfigService;
import com.qunar.corp.cactus.service.governance.config.LoadBalanceConfigService;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static com.alibaba.dubbo.common.Constants.DEFAULT_LOADBALANCE;
import static com.alibaba.dubbo.common.Constants.LOADBALANCE_KEY;
import static com.google.common.base.Strings.emptyToNull;
import static com.qunar.corp.cactus.util.ConstantHelper.NAME;
import static com.qunar.corp.cactus.util.UrlHelper.toKey;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 19:47
 */
@Service
public class LoadBalanceConfigServiceImpl extends AbstractConfigService implements LoadBalanceConfigService {

    private static final Logger logger = LoggerFactory.getLogger(LoadBalanceConfigServiceImpl.class);

    @Override
    public void changeLoadBalance(GovernanceData oldData, String loadBalance) {
        logger.info("change load balance to {} with old data ({})", loadBalance, oldData);
        checkLoadBalance(oldData, loadBalance);

        Map<String, String> params = Maps.newHashMap();
        params.put(LOADBALANCE_KEY, loadBalance);
        configGovernanceService.changeDatas(oldData, params);
    }

    @Override
    @Transactional
    public void setLoadBalance(ServiceSign sign, String loadBalance) {
        logger.info("set load balance to service sign {} with {}", sign, loadBalance);
        checkLoadBalance(loadBalance, sign);
        commonChecker.checkValidState(sign);

        Map<String, String> params = Maps.newHashMap();
        params.put(NAME, LOADBALANCE_KEY);
        params.put(LOADBALANCE_KEY, loadBalance);

        Optional<Long> id = addOneData(sign, params);
        if (!id.isPresent()) {
            throw new RuntimeException("add data but fail with " + sign + ", load balance " + loadBalance);
        }
        configGovernanceService.enable(dao.findById(id.get()));
    }

    private void checkLoadBalance(GovernanceData oldData, String loadBalance) {
        checkLoadBalance(loadBalance, oldData.getServiceSign());
    }

    private void checkLoadBalance(String loadBalance, ServiceSign sign) {
        if (!getAvailableLoadBalances(sign).contains(loadBalance)) {
            logger.info("illegal loadbalance {} with service sign {}", loadBalance, sign);
            throw new IllegalParamException(LOADBALANCE_KEY, loadBalance);
        }
    }

    private Function<String, String> EMPTY_TO_NULL_FUNC = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return emptyToNull(input);
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getAvailableLoadBalances(ServiceSign sign) {
        return Sets.newHashSet(Iterables.concat(
                providerService
                        .getItems(sign.getGroup(), sign.serviceInterface,
                                ProviderPredicates.serviceKeyEqual(sign), UrlHelper.zkIdEqual(sign.zkId))
                        .transform(toKey(LOADBALANCE_KEY))
                        .transform(EMPTY_TO_NULL_FUNC)
                        .filter(Predicates.notNull()),

                consumerService
                        .getItems(sign.getGroup(), sign.serviceInterface,
                                UrlHelper.matchServiceKey(sign), UrlHelper.zkIdEqual(sign.zkId))
                        .transform(toKey(LOADBALANCE_KEY))
                        .transform(EMPTY_TO_NULL_FUNC)
                        .filter(Predicates.notNull()),

                ConstantHelper.ORIGIN_LOAD_BALANCE_VALUES));
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getZkIdServiceKeyMatchedLoadBalance(ServiceSign sign) {
        Optional<String> configLoadBalance = queryService
                .getServiceKeyMatchedGovernanceDatas(
                        GovernanceQuery.createBuilder().zkId(sign.zkId).group(sign.group)
                                .serviceName(sign.serviceInterface).serviceGroup(sign.serviceGroup)
                                .version(sign.version).pathType(PathType.CONFIG).dataType(DataType.ZKDATA)
                                .status(Status.ENABLE).build())
                .transform(UrlHelper.toFullUrl()).transform(toKey(LOADBALANCE_KEY)).transform(EMPTY_TO_NULL_FUNC)
                .filter(Predicates.notNull()).first();
        if (configLoadBalance.isPresent()) {
            return configLoadBalance.get();
        }

        Optional<String> consumerLoadBalance = consumerService
                .getItems(sign.getGroup(), sign.serviceInterface, UrlHelper.matchServiceKey(sign), UrlHelper.zkIdEqual(sign.zkId))
                .transform(toKey(LOADBALANCE_KEY)).transform(EMPTY_TO_NULL_FUNC).filter(Predicates.notNull()).first();
        if (consumerLoadBalance.isPresent()) {
            return consumerLoadBalance.get();
        }

        Optional<String> providerLoadBalance = providerService
                .getItems(sign.getGroup(), sign.serviceInterface, ProviderPredicates.serviceKeyEqual(sign), UrlHelper.zkIdEqual(sign.zkId))
                .transform(toKey(LOADBALANCE_KEY)).transform(EMPTY_TO_NULL_FUNC).filter(Predicates.notNull()).first();
        if (providerLoadBalance.isPresent()) {
            return providerLoadBalance.get();
        }

        return DEFAULT_LOADBALANCE;
    }
}
