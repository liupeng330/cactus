package com.qunar.corp.cactus.service.mock;

import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.web.model.RpcInvokeResult;

/**
 * @author zhenyu.nie created on 2014 2014/9/14 23:44
 */
public interface MockConsumerService {

    RpcInvokeResult invoke(ServiceSign sign, String method, String parameter);
    public String[] getParaTypes(String parameter);
    public Object[][] coneverStringToData(String input);
}
