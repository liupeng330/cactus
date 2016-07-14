package com.qunar.corp.cactus.drainage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicLongMap;
import com.qunar.corp.cactus.drainage.bean.DrainageIpAndPort;
import com.qunar.corp.cactus.drainage.service.ProxyLoadBalancer;
import com.qunar.corp.cactus.drainage.service.RoundRobinProxyLoadBalancer;
import com.qunar.corp.cactus.drainage.tools.GlobalConfig;
import com.qunar.corp.cactus.util.CommonCache;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.qunar.corp.cactus.drainage.constant.Constants.PROXY_ADDRESS_CONFIG_KEY_PREFIX;
import static com.qunar.corp.cactus.drainage.constant.Constants.VERTICAL_SPLITTER;

/**
 * @author sen.chai
 * @date 2015-05-07 13:48
 */
/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/governance.xml"})*/
public class ProxyLoadBalanceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProxyLoadBalanceTest.class);

    private ProxyLoadBalancer proxyLoadBalancer;

    @Test
    public void testBalance() throws Exception {
        List<String> configFiles = Lists.newArrayList("drainage.properties");
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setConfigFiles(configFiles);
        globalConfig.init();
        proxyLoadBalancer = new RoundRobinProxyLoadBalancer();

        List<String> hostList = Lists.newArrayList("l-qmq1.plat.dev.cn6",
                "l-qmq2.plat.dev.cn6", "l-cbrs.plat.dev.cn6", "");

        Set<String> proxySet = Sets.newTreeSet(VERTICAL_SPLITTER.splitToList(GlobalConfig.get(PROXY_ADDRESS_CONFIG_KEY_PREFIX + "cn6")));

        AtomicLongMap<String> proxyResultCount = AtomicLongMap.create();
        Multimap<String, String> multimap = HashMultimap.create();

        for (String host : hostList) {
            String serviceIp = CommonCache.getIpByHostname(host);
            DrainageIpAndPort onlineMachine = DrainageIpAndPort.of(serviceIp, 9001);
            DrainageIpAndPort proxyIpAndPort = proxyLoadBalancer.chooseOne(onlineMachine, "cn6");
            proxyResultCount.incrementAndGet(proxyIpAndPort.getIp());
            multimap.put(proxyIpAndPort.getIp(), serviceIp);
            logger.info("service={}, proxy={}", serviceIp, proxyIpAndPort);
        }

        logger.info("{}", multimap);
        logger.info("{}", proxyResultCount);

        Set<String> chosenProxySet = Sets.newTreeSet(proxyResultCount.asMap().keySet());
        Assert.assertEquals(chosenProxySet, proxySet);


    }
}
