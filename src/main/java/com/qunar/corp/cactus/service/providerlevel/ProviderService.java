package com.qunar.corp.cactus.service.providerlevel;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.Comparator;
import java.util.List;

/**
 * Date: 13-10-30 Time: 上午11:04
 *
 * @author: xiao.liang
 */
public interface ProviderService extends ProviderLevelService {

    List<String> getMethods(String group, String serviceInterface, Predicate<URL>... urlPredicates);

    FluentIterable<URL> getZkIdAndServiceKeyMatchedEnabledProviders(ServiceSign serviceSign);

    URL getConfiguredProvider(URL url);

    FluentIterable<URL> getConfiguredProviders(String group, String serviceInterface, Predicate<URL>... predicates);

    ProviderService distinctWith(Comparator<URL> comparator);
}
