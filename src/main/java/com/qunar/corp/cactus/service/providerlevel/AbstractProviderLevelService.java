package com.qunar.corp.cactus.service.providerlevel;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.qunar.corp.cactus.service.AbstractService;
import com.qunar.corp.cactus.util.UrlHelper;

import java.util.Set;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 14:23
 */
public class AbstractProviderLevelService extends AbstractService implements ProviderLevelService {

    private final String protocol;

    private final String category;

    public AbstractProviderLevelService(String protocol, String category) {
        this.protocol = protocol;
        this.category = category;
    }

    @Override
    public FluentIterable<URL> getItems(String group, Predicate<URL>... predicates) {
        return getItems(group, zkClusterService.getServiceNamesByGroup(group), predicates);
    }

    @Override
    public FluentIterable<URL> getItems(String group, String serviceInterface, Predicate<URL>... predicates) {
        return getItems(group, ImmutableSet.of(serviceInterface), predicates);
    }

    private FluentIterable<URL> getItems(String group, Set<String> serviceNames, Predicate<URL>[] predicates) {
        Predicate<URL> mergedPredicate = UrlHelper.hasProtocol(protocol);
        for (Predicate<URL> predicate : predicates) {
            mergedPredicate = Predicates.and(mergedPredicate, predicate);
        }
        return FluentIterable.from(serviceNames).transformAndConcat(toUrls(group, mergedPredicate));
    }

    private Function<String, Iterable<URL>> toUrls(final String group, final Predicate<URL> predicate) {
        return new Function<String, Iterable<URL>>() {
            @Override
            public Iterable<URL> apply(String service) {
                return zkClusterService.getUrls(group, service, category).filter(predicate);
            }
        };
    }
}
