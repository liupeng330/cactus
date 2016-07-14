package com.qunar.corp.cactus.service.governance.config.impl;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.exception.IllegalParamException;
import com.qunar.corp.cactus.service.governance.config.AbstractConfigService;
import com.qunar.corp.cactus.service.governance.config.WeightConfigService;
import com.qunar.corp.cactus.util.ConstantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.alibaba.dubbo.common.Constants.WEIGHT_KEY;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 20:00
 */
@Service
public class WeightConfigServiceImpl extends AbstractConfigService implements WeightConfigService {

    private static final Logger logger = LoggerFactory.getLogger(WeightConfigServiceImpl.class);

    @Override
    @Transactional
    public void doubleOrHalfWeight(ServiceSign sign, URL provider, int type) {
        String typeStr = type == ConstantHelper.WEIGHT_TYPE_DOUBLE ? "double" : "half";
        logger.info("{} weight of provider {} with service sign {}", typeStr, provider.toFullString(), sign);
        commonChecker.checkValidState(sign);

        Map<String, String> params = Maps.newHashMap();
        int weight = handleWeight(getWeight(providerService.getConfiguredProvider(provider)), type);
        params.put(WEIGHT_KEY, String.valueOf(weight));

        Optional<Long> id = addOneData(sign, params);
        if (!id.isPresent()) {
            throw new RuntimeException("add data fail with " + sign + ", provider " + provider + ", type " + typeStr + " weight");
        }
        configGovernanceService.enable(dao.findById(id.get()));
    }

    private int getWeight(URL url) {
        String weightInStr = url.getParameter(WEIGHT_KEY, "100");
        try {
            return Integer.parseInt(weightInStr);
        } catch (Throwable e) {
            throw new IllegalParamException(WEIGHT_KEY, weightInStr, "weight " + "(" + weightInStr
                    + ") should be integer");
        }
    }

    private int handleWeight(int num, int type) {
        Preconditions.checkArgument(num > 0, "num should be positive: %s", num);
        int result;
        if (type == ConstantHelper.WEIGHT_TYPE_DOUBLE) {
            result = num * 2;
            if (result < num) {
                throw new IllegalParamException(WEIGHT_KEY, String.valueOf(num), "num is too large: " + num);
            }
        } else {
            result = num / 2;
            if (result <= 0) {
                throw new IllegalParamException(WEIGHT_KEY, String.valueOf(num), "num is too small: " + num);
            }
        }
        return result;
    }
}
