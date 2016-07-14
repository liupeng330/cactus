package com.qunar.corp.cactus.service.governance.config.impl;

import com.alibaba.dubbo.common.URL;

/**
 * @author zhenyu.nie created on 2014 2014/8/4 21:33
 * 这个接口主要是因为aop事务不能在一个类的内部启用，必须要放到另外一个类中，由另外一个类来调用这个接口
 */
interface OnlineService {

    void providerOffline(URL provider);

    void providerOnline(URL provider);
}
