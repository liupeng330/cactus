package com.qunar.corp.cactus.service.governance.router;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/18 19:13
 */
public interface RouterPreviewService {

    Map<URL, Map<Invocation, List<Invoker<Object>>>> preview(ServiceSign sign, Map<String, String> params);

    Map<URL, Map<Invocation, List<Invoker<Object>>>> preview(ServiceSign sign);
}
