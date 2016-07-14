package com.qunar.corp.cactus.service.providerlevel;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * Date: 13-10-30 Time: 上午11:04
 * 
 * @author: xiao.liang
 * @description:
 */
public interface ConsumerService extends ProviderLevelService {

    FluentIterable<URL> getZkIdAndServiceKeyMatchedConfiguredConsumers(ServiceSign serviceSign, Predicate<URL>... predicates);
}
