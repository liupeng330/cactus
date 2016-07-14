package com.qunar.corp.cactus.drainage.tools;

import com.qunar.corp.cactus.util.CommonCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sen.chai
 * @date 2015-05-07 17:25
 */
public class IpHostHelper {

    private static final Logger logger = LoggerFactory.getLogger(IpHostHelper.class);

    private static final String IPV4_PATTERN = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";

    public static String toIpIfHost(String input) {
        if (input.matches(IPV4_PATTERN)) {
            return input;
        }
        return CommonCache.getIpByHostname(input);
    }

    public static String toHostIfIp(String input) {
        if (input.matches(IPV4_PATTERN)) {
            return CommonCache.getHostNameByIp(input);
        }
        return input;
    }

}
