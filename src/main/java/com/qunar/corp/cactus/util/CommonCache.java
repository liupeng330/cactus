package com.qunar.corp.cactus.util;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qunar.corp.cactus.bean.IpAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-11-15
 * Time: 下午4:54
 */
public class CommonCache {

    private static Logger log = LoggerFactory.getLogger(CommonCache.class);

    private static final LoadingCache<String, String> ipCache = CacheBuilder.newBuilder().maximumSize(80000)
            .expireAfterWrite(12, TimeUnit.HOURS).build(
                    new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) throws Exception {
                            try {
                                InetAddress ia = InetAddress.getByName(key);
                                String name = ia.getHostName();// 获取计算机主机名
                                int qunar = name.indexOf("qunar");
                                if (qunar > 0) {
                                    name = name.substring(0, qunar - 1);
                                }
                                return name;
                            } catch (UnknownHostException e) {
                                return key;
                            }
                        }
                    }
            );

    private static final LoadingCache<String, String> hostnameCache = CacheBuilder.newBuilder().maximumSize(5000).build(
            new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    try {
                        InetAddress ia = InetAddress.getByName(key);
                        return ia.getHostAddress();
                    } catch (UnknownHostException e) {
                        return key;
                    }
                }
            }
    );

    public static String getHostNameByIp(String ip) {
        if (Strings.isNullOrEmpty(ip)) {
            return ip;
        }

        return ipCache.getUnchecked(ip);
    }

    public static String address2HostNameAndPort(String address) {
        if (Strings.isNullOrEmpty(address)) {
            return address;
        }
        IpAndPort ipAndPort = IpAndPort.fromString(address);
        if (!ipAndPort.hasPort()) {
            return CommonCache.getHostNameByIp(ipAndPort.getIp());
        }
        return getHostNameByIp(ipAndPort.getIp()) + ":" + ipAndPort.getPort();
    }

    public static String getIpByHostname(String hostname) {
        if (Strings.isNullOrEmpty(hostname)) {
            return hostname;
        }

        return hostnameCache.getUnchecked(hostname);
    }

}
