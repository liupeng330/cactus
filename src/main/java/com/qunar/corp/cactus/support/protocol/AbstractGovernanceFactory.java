package com.qunar.corp.cactus.support.protocol;

import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ServiceSign;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2014 2014/8/4 12:53
 */
public abstract class AbstractGovernanceFactory implements GovernanceFactory {

    protected abstract ProtocolBuilder createBuilder();

    @Override
    public GovernanceData create(ServiceSign sign, Map<String, String> params) {
        return createBuilder().zkId(sign.zkId).ip(sign.getIp()).port(sign.getPort())
                .registryKey(sign.getGroup()).serviceGroup(sign.serviceGroup).service(sign.serviceInterface)
                .version(sign.version).addParams(params).build();
    }
}
