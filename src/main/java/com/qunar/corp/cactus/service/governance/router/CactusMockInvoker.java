package com.qunar.corp.cactus.service.governance.router;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * @author zhenyu.nie created on 2013 13-12-4 下午7:19
 */
public class CactusMockInvoker<T> implements Invoker<T> {

    private final URL url;

    public CactusMockInvoker(URL url) {
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CactusMockInvoker)) return false;

        CactusMockInvoker that = (CactusMockInvoker) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
