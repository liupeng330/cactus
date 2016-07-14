package com.qunar.corp.cactus.support.protocol;

import static com.alibaba.dubbo.common.Constants.*;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-11-15
 * Time: 上午11:36
 */
public class OverrideBuilder extends AbstractProtocolBuilder {

    @Override
    protected String getCategory() {
        return CONFIGURATORS_CATEGORY;
    }

    @Override
    protected String getProtocol() {
        return OVERRIDE_PROTOCOL;
    }

    @Override
    protected Map<String, String> getOriginParams() {
        Map<String, String> resultParams = Maps.newHashMap(params);
        if (ANY_VALUE.equals(resultParams.get(APPLICATION_KEY))) {
            resultParams.remove(APPLICATION_KEY);
        }
        return resultParams;
    }
}
