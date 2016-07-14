package com.qunar.corp.cactus.service.governance.config;

import com.alibaba.dubbo.common.URL;

import java.util.List;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 21:09
 */
public interface ProviderOnlineService {

    List<URL> providerOffline(List<URL> providers);

    void providerOnline(List<URL> providers);

    void providerOffline(URL provider);

    void providerOnline(URL provider);
}
