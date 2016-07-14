package com.qunar.corp.cactus.service.providerlevel;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 14:23
 */
public interface ProviderLevelService {

    FluentIterable<URL> getItems(String group, String serviceInterface, Predicate<URL>... predicates);

    FluentIterable<URL> getItems(String group, Predicate<URL>... predicates);
}
