/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web.api;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.qunar.corp.cactus.bean.ServiceStatusEnum;
import com.qunar.corp.cactus.monitor.SystemCounter;
import com.qunar.corp.cactus.service.ZKClusterService;
import com.qunar.corp.cactus.service.governance.config.ProviderOnlineService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.web.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import static com.alibaba.dubbo.common.Constants.GROUP_KEY;
import static com.qunar.corp.cactus.util.ConstantHelper.*;

/**
 * @author zhenyu.nie created on 2014 14-1-7 下午3:05
 * 效果是当服务处于上线状态，api调用上线则上线，api调用下线则下线
 * 当服务处于下线状态，api调用上线和下线都是下线
 */
@Controller
@RequestMapping("/cactusapi")
public class DubboServiceRegisterApiController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(DubboServiceRegisterApiController.class);

    private static final int UNEXPECTED_EXCEPTION_CODE = 101;

    private static final int ILLEGAL_ACCESS_CODE = 102;

    private static final String UNEXPECTED_EXCEPTION_INFO = "An unexpected exception occurred. " +
            "Please check your param then try again or contact with us!";

    @Resource
    private ZKClusterService zkClusterService;

    @Resource
    private ProviderOnlineService onlineService;

    @RequestMapping("/online")
    @ResponseBody
    public Object online(@RequestParam("registry") String registry, @RequestParam("provider") String provider) {
        UserContainer.setUserId(ConstantHelper.CACTUS_API_ID);
        UserContainer.setUserName(ConstantHelper.CACTUS_API);
        try {
            return changeServiceStatus(registry, provider, ServiceStatusEnum.ONLINE);
        } finally {
            UserContainer.remove();
        }
    }

    @RequestMapping("/offline")
    @ResponseBody
    public Object offline(@RequestParam("registry") String registry, @RequestParam("provider") String provider) {
        UserContainer.setUserId(ConstantHelper.CACTUS_API_ID);
        UserContainer.setUserName(ConstantHelper.CACTUS_API);
        try {
            return changeServiceStatus(registry, provider, ServiceStatusEnum.OFFLINE);
        } finally {
            UserContainer.remove();
        }
    }

    private Object changeServiceStatus(String registryStr, String providerStr, ServiceStatusEnum serviceStatus) {
        logger.info("api {} to provider ({}) with registry ({})", serviceStatus.getText(), providerStr, registryStr);
        checkEmpty(registryStr, providerStr);
        URL registry = URL.valueOf(registryStr);
        URL provider = URL.valueOf(providerStr);

        return changeServiceStatus(registry, provider, serviceStatus);
    }

    private Object changeServiceStatus(URL registry, URL provider, ServiceStatusEnum serviceStatus) {
        logger.info("api {} to provider ({}) with registry ({})", serviceStatus.getText(), provider, registry);
        SystemCounter.apiCallOperation.increment();

        Optional<Long> zkId = zkClusterService.findZkId(registry);
        if (!zkId.isPresent()) {
            logger.info("api {} to provider ({}) with registry ({}), can not find zk", serviceStatus.getText(),
                    provider, registry);
            SystemCounter.apiCallZkNotFind.increment();
            return oldErrorJson(ILLEGAL_ACCESS_CODE, "illegal access, can not find zk with registry " + registry);
        }

        try {
            // 因为在dubbo配置文件不填registry的group的时候，默认为“dubbo”，但是在provider自己获取registry url上又不会有，这里得加上
            if (Strings.isNullOrEmpty(registry.getParameter(GROUP_KEY))) {
                registry = registry.addParameter(GROUP_KEY, "dubbo");
            }

            checkValid(registry, provider);
            changeStatus(makeParam(registry, provider, zkId.get()), serviceStatus);
            return dataJson();
        } catch (Throwable e) {
            logger.error("occur error when api {} with provider ({}) with registry ({})", serviceStatus.getText(),
                    provider, registry);
            SystemCounter.apiCallFailedOperation.increment();
            return oldErrorJson(UNEXPECTED_EXCEPTION_CODE, UNEXPECTED_EXCEPTION_INFO);
        }
    }

    private void changeStatus(URL paramUrl, ServiceStatusEnum serviceStatus) {
        if (serviceStatus == ServiceStatusEnum.ONLINE) {
            onlineService.providerOnline(paramUrl);
        } else {
            onlineService.providerOffline(paramUrl);
        }
    }

    private URL makeParam(URL registry, URL provider, Long zkId) {
        return provider.addParameter(ZKID, zkId).addParameter(CACTUS_USER_NAME, CACTUS_API)
                .addParameter(CACTUS_USER_ID, CACTUS_API_ID)
                .addParameter(Q_REGISTRY_KEY, registry.getParameter(GROUP_KEY));
    }

    private void checkEmpty(String registryStr, String providerStr) {
        if (Strings.isNullOrEmpty(registryStr)) {
            logger.warn("empty registryStr");
            throw new IllegalArgumentException("empty registryStr");
        }
        if (Strings.isNullOrEmpty(providerStr)) {
            logger.warn("empty providerStr");
            throw new IllegalArgumentException("empty providerStr");
        }
    }

    private void checkValid(URL registry, URL provider) {
        if (!validRegistry(registry)) {
            logger.warn("not valid registry {}", registry);
            throw new IllegalArgumentException("not valid registry " + registry);
        }

        if (!validProvider(provider)) {
            logger.warn("not valid provider ({})", provider);
            throw new IllegalArgumentException("not valid provider " + provider);
        }
    }

    private boolean validRegistry(URL registry) {
        return !Strings.isNullOrEmpty(registry.getParameter(GROUP_KEY));
    }

    private boolean validProvider(URL provider) {
        return provider.getPort() > 0;
    }
}
