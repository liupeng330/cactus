package com.qunar.corp.cactus.web.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Date: 14-2-19
 * Time: 下午2:45
 *
 * @author: xiao.liang
 * @description:
 */
public class InvokeRelationship {
    private final List<ConsumerSign> consumerSignList;
    private final List<Provider> providerList;

    public InvokeRelationship(Iterable<Provider> providers, List<ConsumerSign> consumerSigns) {
        providerList = ImmutableList.copyOf(providers);
        consumerSignList = ImmutableList.copyOf(consumerSigns);
    }

    public List<ConsumerSign> getConsumerSignList() {
        return consumerSignList;
    }

    public List<Provider> getProviderList() {
        return providerList;
    }
}
