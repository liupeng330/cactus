package com.qunar.corp.cactus.service.governance.router.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.service.governance.router.RouterAssistService;
import com.qunar.corp.cactus.service.governance.router.RouterHelper;
import com.qunar.corp.cactus.service.governance.router.RouterPreviewService;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.protocol.GovernanceFactory;
import com.qunar.corp.cactus.util.UrlHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 18:42
 */
@Service
public class RouterPreviewServiceImpl implements RouterPreviewService {

    @Resource
    private ConsumerService consumerService;

    @Resource
    private ProviderService providerService;

    @Resource
    private RouterAssistService routerAssistService;

    @Resource(name = "routerFactory")
    private GovernanceFactory routerFactory;

    // 可能的Exception: IllegalParamException
    @Override
    public Map<URL, Map<Invocation, List<Invoker<Object>>>> preview(ServiceSign sign, Map<String, String> params) {
        Preconditions.checkNotNull(sign, "sign can not be null");
        Preconditions.checkNotNull(params, "params can not be null");
        Map<String, String> realParams = routerAssistService.checkAndExtractValidParams(params);

        GovernanceData data = routerFactory.create(sign, realParams);
        List<GovernanceData> routers = Lists.newArrayList(routerAssistService.getRelativeDatas(sign));
        routers.add(data);
        return preview(sign, routers);
    }

    @Override
    public Map<URL, Map<Invocation, List<Invoker<Object>>>> preview(ServiceSign sign) {
        Preconditions.checkNotNull(sign, "sign can not be null");
        return preview(sign, routerAssistService.getRelativeDatas(sign));
    }

    @SuppressWarnings("unchecked")
    private Map<URL, Map<Invocation, List<Invoker<Object>>>> preview(ServiceSign sign, List<GovernanceData> routers) {
        List<URL> consumers = consumerService.getItems(sign.getGroup(), sign.serviceInterface,
                UrlHelper.matchServiceKey(sign), UrlHelper.zkIdEqual(sign.zkId)).toList();
        List<URL> providers = providerService.getZkIdAndServiceKeyMatchedEnabledProviders(sign).toList();
        return RouterHelper.route(consumers, providers, routers);
    }
}
