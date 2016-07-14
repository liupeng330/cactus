package com.qunar.corp.cactus.drainage.service;

import com.qunar.corp.cactus.drainage.bean.DrainageIpAndPort;

/**
 * @author sen.chai
 * @date 2015-04-23 20:14
 */
public interface ProxyLoadBalancer {

    DrainageIpAndPort chooseOne(DrainageIpAndPort serviceIpAndPort, String roomId);

}
