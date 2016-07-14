package com.qunar.corp.cactus.web.model;

/**
 * @author zhenyu.nie created on 2014 2014/9/16 18:47
 */
public class RpcInvokeResult {

    private Object result;

    private long elapsedTime;

    public RpcInvokeResult(Object result, long elapsedTime) {
        this.result = result;
        this.elapsedTime = elapsedTime;
    }

    public Object getResult() {
        return result;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}
