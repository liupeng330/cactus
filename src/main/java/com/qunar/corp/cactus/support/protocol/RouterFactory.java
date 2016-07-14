package com.qunar.corp.cactus.support.protocol;

import org.springframework.stereotype.Service;

/**
 * @author zhenyu.nie created on 2014 2014/8/4 16:43
 */
@Service
public class RouterFactory extends AbstractGovernanceFactory implements GovernanceFactory {

    @Override
    protected ProtocolBuilder createBuilder() {
        return new RouterBuilder();
    }
}
