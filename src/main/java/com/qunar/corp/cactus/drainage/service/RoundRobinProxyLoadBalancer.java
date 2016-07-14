package com.qunar.corp.cactus.drainage.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.drainage.bean.DrainageIpAndPort;
import com.qunar.corp.cactus.drainage.tools.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import static com.qunar.corp.cactus.drainage.constant.Constants.*;

/**
 * @author sen.chai
 * @date 2015-04-23 20:17
 */
@Service
public class RoundRobinProxyLoadBalancer implements ProxyLoadBalancer {

    private static final Logger logger = LoggerFactory.getLogger(RoundRobinProxyLoadBalancer.class);

    private static final AtomicLong counter = new AtomicLong();


    @Override
    public DrainageIpAndPort chooseOne(DrainageIpAndPort serviceIpAndPort, String roomId) {
        List<DrainageIpAndPort> proxyIps = getProxyIpAndPort(roomId);
        return proxyIps.get((int)Math.abs(counter.incrementAndGet() % proxyIps.size()));
    }


    private List<DrainageIpAndPort> getProxyIpAndPort(String roomId) {
        TreeSet<DrainageIpAndPort> result = Sets.newTreeSet();
        int port = GlobalConfig.getInt(PROXY_PORT_CONFIG_KEY);
        Iterable<String> iterable = VERTICAL_SPLITTER.split(GlobalConfig.get(PROXY_ADDRESS_CONFIG_KEY_PREFIX + roomId));
        for (String proxyIp : iterable) {
            result.add(new DrainageIpAndPort(proxyIp, port));
        }
        if (result.size() == 0) {
            final String message = "can't find any proxy server!";
            logger.error(message);
            throw new RuntimeException(message);
        }
        return Lists.newArrayList(result);
    }

}
