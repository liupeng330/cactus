package com.qunar.corp.cactus.drainage.service;

import com.qunar.corp.cactus.drainage.bean.DrainageGroup;
import com.qunar.corp.cactus.drainage.bean.DrainageIpAndPort;

import java.util.List;
import java.util.Set;

/**
 * @author sen.chai
 * @date 2015-05-10 15:15
 */
public interface DrainageManager {

    void stopDrainage(String service, String methodName, long zkId, String serviceKey, String group);

    void startDrainage(String service, String methodName, long zkId, String serviceKey, String group,
                       Set<DrainageGroup> targetSet, Set<DrainageIpAndPort> providerSet);

    boolean serviceIsDrainaging(String serviceName);

    List<DrainageIpAndPort> getProviderAddress(String service, String methodName, long zkId, String serviceKey, String group);

    Object getBetaAddress(String service, String methodName, long zkId, String serviceKey, String group);
}
