package com.qunar.corp.cactus.web.model;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Date: 14-2-19
 * Time: 下午2:30
 *
 * @author: xiao.liang
 * @description:
 */
public class ConsumerSign {
    private final URL url;
    private final Set<String> methods;
    private final Consumer consumer;

    public ConsumerSign(Consumer consumer, Set<String> methods) {
        Preconditions.checkNotNull(consumer);
        this.consumer = consumer; // 注意：这里转化出的consumer的group为null
        this.url = consumer.getRealUrl();
        this.methods = ImmutableSet.copyOf(methods);
    }

    public URL getUrl() {
        return url;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public String getApplication() {
        return url.getParameter(Constants.APPLICATION_KEY);
    }

    public String getMethodsStr() {
        if (methods == null || methods.size() == 0) {
            return "";
        }
        String methodsStr = methods.toString();
        int size = methodsStr.length();
        return methodsStr.substring(1).substring(0,size-2);
    }

    public Consumer getConsumer() {
        return consumer;
    }
}
