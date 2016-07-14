package com.qunar.corp.cactus.service.governance.config;

import com.alibaba.dubbo.common.URL;
import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 19:59
 */
public interface WeightConfigService {

    void doubleOrHalfWeight(ServiceSign sign, URL provider, int type);
}
