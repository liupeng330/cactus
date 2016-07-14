package com.qunar.corp.cactus.support.protocol;

import com.qunar.corp.cactus.bean.GovernanceData;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-11-15
 * Time: 上午11:42
 */
public interface ProtocolBuilder {

    ProtocolBuilder zkId(long zkId);

    ProtocolBuilder ip(String ip);

    ProtocolBuilder port(int port);

    ProtocolBuilder service(String serviceName);

    ProtocolBuilder registryKey(String key);

    ProtocolBuilder serviceGroup(String serviceGroup);

    ProtocolBuilder version(String version);

    ProtocolBuilder addParams(Map<String, String> params);

    ProtocolBuilder withKeyValue(String key, String value);

    GovernanceData build();
}
