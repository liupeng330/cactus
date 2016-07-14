package com.qunar.corp.cactus.service.providerlevel.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Configurator;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.service.providerlevel.AbstractProviderLevelService;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.alibaba.dubbo.common.Constants.*;

/**
 * Date: 13-10-30 Time: 上午11:06
 * 
 * @author: xiao.liang
 * @description:
 */
@Service
public class ConsumerServiceImpl extends AbstractProviderLevelService implements ConsumerService {

    @Resource
    private IQueryGovernanceService queryGovernanceService;

    public ConsumerServiceImpl() {
        super(CONSUMER_PROTOCOL, CONSUMERS_CATEGORY);
    }

    @Override
    public FluentIterable<URL> getZkIdAndServiceKeyMatchedConfiguredConsumers(final ServiceSign sign,
                                                                              Predicate<URL>... predicates) {
        FluentIterable<URL> consumers = getItems(sign.getGroup(), sign.serviceInterface, predicates)
                .filter(UrlHelper.zkIdEqual(sign.zkId)).filter(ProviderPredicates.serviceKeyEqual(sign));
        if (consumers.isEmpty()) {
            return FluentIterable.from(ImmutableList.<URL> of());
        }

        final FluentIterable<URL> configs = queryGovernanceService
                .getServiceKeyMatchedGovernanceDatas(
                        GovernanceQuery.createBuilder().zkId(sign.zkId).group(sign.group)
                                .serviceName(sign.serviceInterface).pathType(PathType.CONFIG)
                                .serviceGroup(sign.serviceGroup).version(sign.version).status(Status.ENABLE)
                                .dataType(DataType.ZKDATA).build()).transform(UrlHelper.toFullUrl())
                .filter(Predicates.and(predicates));
        return consumers.transform(new Function<URL, URL>() {
            @Override
            public URL apply(URL input) {
                return input.addParameters(getConsumerParams(input, configs));
            }
        });
    }

    private Map<String, String> getParams(URL url, FluentIterable<URL> unMatchedConfigs) {
        ServiceSign consumer = ServiceSign.makeServiceSign(url);
        if (!validConsumer(consumer)) {
            throw new IllegalArgumentException("not valid consumer: (" + consumer + ")");
        }
        List<Configurator> configurators = toConfigurators(unMatchedConfigs
                .filter(UrlHelper.matchSubscriber(url.addParameter(CATEGORY_KEY, PROVIDERS_CATEGORY + ","
                        + CONFIGURATORS_CATEGORY + "," + ROUTERS_CATEGORY))));
        for (Configurator configurator : configurators) {
            url = configurator.configure(url);
        }
        return url.getParameters();
    }

    private boolean validConsumer(ServiceSign consumer) {
        return !consumer.hasPort();
    }

    private Map<String, String> getConsumerParams(URL url, FluentIterable<URL> configs) {
        Map<String, String> allParams = getParams(url, configs);
        Map<String, String> consumerParams = Maps.newHashMap();
        consumerParams.put(ConstantHelper.MOCK_KEY_ON_CONFIG_URL, getDefaultValueWhenNull(allParams.get(MOCK_KEY), ""));
        return consumerParams;
    }

}
