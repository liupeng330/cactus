package com.qunar.corp.cactus.service.providerlevel.impl;

import com.qunar.corp.cactus.service.providerlevel.AbstractProviderLevelService;
import com.qunar.corp.cactus.service.providerlevel.ProviderLevelService;
import org.springframework.stereotype.Service;

import static com.alibaba.dubbo.common.Constants.*;

/**
 * @author zhenyu.nie created on 2014 2014/9/1 10:43
 */
@Service
public class ConfiguratorService extends AbstractProviderLevelService implements ProviderLevelService {

    public ConfiguratorService() {
        super(OVERRIDE_PROTOCOL, CONFIGURATORS_CATEGORY);
    }
}
