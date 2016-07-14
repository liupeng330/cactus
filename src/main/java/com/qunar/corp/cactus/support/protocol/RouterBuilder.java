package com.qunar.corp.cactus.support.protocol;

import static com.alibaba.dubbo.common.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.qunar.corp.cactus.service.governance.router.RouterConstants.MATCH_AND_MISMATCH_KEYS;
import static com.qunar.corp.cactus.service.governance.router.RouterConstants.OTHER_KEYS;
import static com.qunar.corp.cactus.service.governance.router.RouterHelper.toRouteRule;

import java.util.Map;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-12-5 Time: 上午11:32
 */
public class RouterBuilder extends AbstractProtocolBuilder {

    public RouterBuilder() {
        this.ip = ANYHOST_VALUE;
        this.port = 0;
    }

    @Override
    public ProtocolBuilder ip(String ip) {
        return this;
    }

    @Override
    public ProtocolBuilder port(int port) {
        return this;
    }

    @Override
    protected String getProtocol() {
        return Constants.ROUTE_PROTOCOL;
    }

    @Override
    protected String getCategory() {
        return Constants.ROUTERS_CATEGORY;
    }

    @Override
    protected Map<String, String> getOriginParams() {
        Map<String, String> resultParams = Maps.newHashMap(Maps.filterKeys(params, Predicates.in(OTHER_KEYS)));
        resultParams.put(ROUTER_KEY, CONDITION_PROTOCOL);
        if (!isNullOrEmpty(resultParams.get(PRIORITY_KEY))) {
            resultParams.put(PRIORITY_KEY, "0");
        }
        resultParams.put(FORCE_KEY, "true");
        resultParams.put(RUNTIME_KEY, "false");
        resultParams.put(RULE_KEY,
                URL.encode(toRouteRule(Maps.filterKeys(params, Predicates.in(MATCH_AND_MISMATCH_KEYS)))));
        return resultParams;
    }
}
