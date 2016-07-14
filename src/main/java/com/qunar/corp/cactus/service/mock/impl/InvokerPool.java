package com.qunar.corp.cactus.service.mock.impl;

import com.alibaba.dubbo.rpc.service.GenericService;
import com.qunar.corp.cactus.bean.ServiceSign;

/**
 * @author zhenyu.nie created on 2014 2014/9/15 0:04
 */
interface InvokerPool {

    GenericService getInvoker(ServiceSign sign);
}
