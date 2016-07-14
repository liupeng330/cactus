package com.qunar.corp.cactus.service.mock.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.*;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.monitor.SystemCounter;
import com.qunar.corp.cactus.service.mock.exception.FailGetInvokerException;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenyu.nie created on 2014 2014/9/15 0:39
 */
@Service
class InvokerPoolImpl implements InvokerPool {

    @Resource
    private ProviderService providerService;

    private static final Logger logger = LoggerFactory.getLogger(InvokerPoolImpl.class);

    private final LoadingCache<ServiceSign, ReferenceConfig<GenericService>> cache = CacheBuilder.newBuilder()
            .maximumSize(200).expireAfterAccess(60, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<ServiceSign, ReferenceConfig<GenericService>>() {
                @Override
                public void onRemoval(RemovalNotification<ServiceSign, ReferenceConfig<GenericService>> notification) {
                    notification.getValue().destroy();
                }
            })
            .build(new CacheLoader<ServiceSign, ReferenceConfig<GenericService>>() {
                @Override
                public ReferenceConfig<GenericService> load(ServiceSign sign) throws Exception {
                    ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
                    reference.setApplication(new ApplicationConfig("cactus-mock-consumer"));
                    reference.setUrl("dubbo://" + sign.getAddress() + "?scope=remote");
                    reference.setInterface(sign.getServiceInterface());
                    reference.setGroup(sign.getServiceGroup());
                    reference.setVersion(sign.getVersion());
                    reference.setTimeout(MockConstants.DEFAULT_TIMEOUT);
                    reference.setGeneric(true);
                    reference.setCheck(true);
                    reference.get();
                    SystemCounter.buildNewMockRef.increment();
                    return reference;
                }
            });

    @Override
    public GenericService getInvoker(ServiceSign sign) {
        if (!isValidProvider(sign)) {
            throw new FailGetInvokerException("no provider: " + sign.getAddress());
        }

        try {
            return cache.get(sign).get();
        } catch (Exception e) {
            logger.warn("can not build reference with {}", sign, e);
            throw new FailGetInvokerException("can not build reference with " + sign, e);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isValidProvider(ServiceSign sign) {
        Predicate<URL> predicate = Predicates.and(
                UrlHelper.zkIdEqual(sign.getZkId()),
                ProviderPredicates.serviceKeyEqual(sign),
                ProviderPredicates.addressEqual(sign.getAddress()));
        return providerService.getItems(sign.getGroup(), sign.getServiceInterface(), predicate).first().isPresent();
    }
}
