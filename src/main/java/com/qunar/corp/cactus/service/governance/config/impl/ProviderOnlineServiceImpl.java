package com.qunar.corp.cactus.service.governance.config.impl;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.bean.ZKCluster;
import com.qunar.corp.cactus.event.UserOperationEvent;
import com.qunar.corp.cactus.exception.LastProviderException;
import com.qunar.corp.cactus.service.governance.config.AbstractConfigService;
import com.qunar.corp.cactus.service.governance.config.ProviderOnlineService;
import com.qunar.corp.cactus.support.UserContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 21:09
 */
@Service
public class ProviderOnlineServiceImpl extends AbstractConfigService implements ProviderOnlineService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderOnlineServiceImpl.class);

    @Resource
    private EventBus eventBus;

    @Resource
    private OnlineService onlineService;

    @Override
    public void providerOnline(List<URL> providers) {
        logger.info("online services {}", providers);
        for (URL provider : providers) {
            try {
                providerOnline(provider);
            } catch (RuntimeException e) {
                logger.error("online provider ({}) fail", provider, e);
                throw e;
            }
        }
    }

    @Override
    public void providerOffline(URL provider) {
        ServiceSign sign = ServiceSign.makeServiceSign(provider);
        Optional<ZKCluster> zkCluster = zkClusterService.findZkCluster(sign.getZkId());
        if (!zkCluster.isPresent()) {
            throw new IllegalArgumentException("无效的zk id");
        }
        onlineService.providerOffline(provider);
        eventBus.post(new UserOperationEvent(
                UserContainer.getUserId(),
                sign,
                zkCluster.get().getName() + "下线provider: serviceKey = [" + sign.getServiceKey() + "]"));
    }

    @Override
    public void providerOnline(URL provider) {
        ServiceSign sign = ServiceSign.makeServiceSign(provider);
        Optional<ZKCluster> zkCluster = zkClusterService.findZkCluster(sign.getZkId());
        if (!zkCluster.isPresent()) {
            throw new IllegalArgumentException("无效的zk id");
        }
        onlineService.providerOnline(provider);
        eventBus.post(new UserOperationEvent(
                UserContainer.getUserId(),
                sign,
                zkCluster.get().getName() + "上线provider: serviceKey = [" + sign.getServiceKey() + "]"));
    }

    @Override
    public List<URL> providerOffline(List<URL> providers) {
        logger.info("offline services {}", providers);
        List<URL> lastProviders = Lists.newArrayList();
        for (URL provider : providers) {
            try {
                providerOffline(provider);
            } catch (LastProviderException e) {
                lastProviders.add(provider);
            } catch (RuntimeException e) {
                logger.error("offline provider ({}) fail", provider, e);
                throw e;
            }
        }
        return lastProviders;
    }
}
