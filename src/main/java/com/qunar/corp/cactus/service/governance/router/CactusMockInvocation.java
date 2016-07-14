package com.qunar.corp.cactus.service.governance.router;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2013 13-12-4 下午7:20
 */
public class CactusMockInvocation implements Invocation {

    private final String method;

    public CactusMockInvocation(String method) {
        this.method = method;
    }

    @Override
    public String getMethodName() {
        return method;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] getArguments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getAttachments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttachment(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Invoker<?> getInvoker() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CactusMockInvocation)) return false;

        CactusMockInvocation that = (CactusMockInvocation) o;

        if (method != null ? !method.equals(that.method) : that.method != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return method != null ? method.hashCode() : 0;
    }
}
