package com.qunar.corp.cactus.service.mock.impl;

import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.service.mock.MockConsumerService;
import com.qunar.corp.cactus.web.model.RpcInvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/9/14 23:49
 */
@Service
class MockConsumerServiceImpl implements MockConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(MockConsumerServiceImpl.class);

    @Resource
    private InvokerPool invokerPool;

    @Override
    @SuppressWarnings("all")
    public RpcInvokeResult invoke(ServiceSign sign, String method, String parameter) {
        logger.info("mock invoke, {}, method={}, parameter={}", sign, method, parameter);

        Preconditions.checkArgument(sign != null, "sign can not be null");
        Preconditions.checkArgument(sign.getAddress() != null, "provider address can not be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(method), "method can not be null or empty");

        String[] types = getParaTypes(parameter);
        Object[] params = getParas(parameter);

        if (types.length != params.length) {
            throw new IllegalArgumentException("param length do not equal to type length");
        }

        try {
            GenericService invoker = invokerPool.getInvoker(sign);
            long start = System.currentTimeMillis();
            Object result = invoker.$invoke(method, types, params);
            return new RpcInvokeResult(result, System.currentTimeMillis() - start);
        } catch (RpcException e) {
            handleException(e, sign);
            return null;
        }
    }

    // 这里用来把一大串的异常信息转成用户容易理解的东西
    // 虽然丑，但是没办法，信息都是在exception的message里面
    private void handleException(RpcException e, ServiceSign sign) {
        if (e.getCause() != null && e.getCause() instanceof RemotingException) {
            RemotingException re = (RemotingException) e.getCause();

            if (re.getMessage() != null
                    && re.getMessage().startsWith("java.lang.IllegalArgumentException: args.length != types.length")) {
                throw new IllegalArgumentException("wrong param length");
            }

            if (re.getMessage() != null
                    && re.getMessage().startsWith("com.alibaba.dubbo.rpc.RpcException: " + sign.getServiceInterface())) {
                int tipStart = re.getMessage().indexOf("java.lang.NoSuchMethodException: " + sign.getServiceInterface());
                if (tipStart >= 0) {
                    int tipEnd = re.getMessage().indexOf("\n", tipStart);
                    if (tipEnd > tipStart) {
                        throw new IllegalArgumentException(re.getMessage().substring(tipStart, tipEnd));
                    }
                }
            }
        }

        if (e.getCause() != null && e.getCause() instanceof TimeoutException) {
            throw new IllegalArgumentException("time out, over " + MockConstants.DEFAULT_TIMEOUT + "ms");
        }
        throw e;
    }

    private static final String[] EMPTY_STR_ARRAY = new String[0];
    private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];

    @SuppressWarnings("all")
    public String[] getParaTypes(String parameter) {
        if (Strings.isNullOrEmpty(parameter)) {
            return EMPTY_STR_ARRAY;
        }

        try {
            Map<String, Object> object = (Map<String, Object>) asMap(JSON.parseObject(parameter));
            Object types = object.get("types");
            if (types != null) {
                return ((List<Object>) types).toArray(EMPTY_STR_ARRAY);
            } else {
                return EMPTY_STR_ARRAY;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("can not resolve method param types");
        }
    }

    @SuppressWarnings("all")
    private Object[] getParas(String parameter) {
        if (Strings.isNullOrEmpty(parameter)) {
            return EMPTY_OBJ_ARRAY;
        }

        try {
            Map<String, Object> object = (Map<String, Object>) asMap(JSON.parseObject(parameter));
            Object params = object.get("params");
            if (params != null) {
                return ((List<Object>) params).toArray(EMPTY_OBJ_ARRAY);
            } else {
                return EMPTY_OBJ_ARRAY;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("can not resolve method params");
        }
    }

    private Object asMap(Object result) {
        if (!(result instanceof JSONObject)) {
            return result;
        }

        Map<String, Object> map = Maps.newHashMap();
        JSONObject jsonObject = (JSONObject) result;
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            if (entry.getValue() instanceof JSONObject) {
                map.put(entry.getKey(), asMap(entry.getValue()));
            } else if (entry.getValue() instanceof JSONArray) {
                JSONArray array = (JSONArray) entry.getValue();
                List<Object> list = Lists.newArrayListWithCapacity(array.size());
                for (Object o : array) {
                    list.add(asMap(o));
                }
                map.put(entry.getKey(), list);
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }
    public Object[][] coneverStringToData(String input){
        if(Strings.isNullOrEmpty(input)){
            Object[][] nullObjectArray = new Object[1][0];
            nullObjectArray[0] = new Object[0];
            return nullObjectArray;
        }
        String[] jsons = input.split("\\n");
        Object[][] result = new Object[jsons.length][];
        for(int i = 0 ; i<jsons.length; i ++){
            String json = jsons[i];

            JSONArray jsonArray =  JSONArray.parseArray(json);
            Object[] subResut = new Object[jsonArray.size()];
            for(int j=0;j<jsonArray.size();j++){
               subResut[j] = asMap(jsonArray.get(j));
            }
            result[i] = subResut;
        }
        return result;
    }
}
