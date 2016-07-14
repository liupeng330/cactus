package com.qunar.corp.cactus.bean;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * Date: 13-12-10 Time: 下午4:47
 *
 * @author: xiao.liang
 * @description:
 */
public class IpAndPort {

    private final String ip;
    private final int port;

    private IpAndPort(String ip, Integer port) {
        this.ip = ip;
        this.port = port == null ? 0 : port;
    }

    // 将"ip:port"格式的字符串转化成IpAndPort对象
    public static IpAndPort fromString(String address) {
        Preconditions.checkArgument(ADDRESS_PATTERN.matcher(address).find(),
                "ipPortString [%s] is not a valid ipPortString", address);
        int indexOfColon = address.indexOf(":");
        if (indexOfColon != -1) {
            return new IpAndPort(address.substring(0, indexOfColon),
                    Integer.parseInt(address.substring(indexOfColon + 1)));
        } else {
            return new IpAndPort(address, 0);
        }
    }

    public static IpAndPort fromPart(String ip, Integer port) {
        Preconditions.checkArgument(isLegalIp(ip), "invalid ip [%s]", ip);
        Preconditions.checkArgument(isValidPort(port), "invalid port: %s", port);
        return new IpAndPort(ip, port);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        String address = ip;
        if (port > 0) {
            address += ":" + port;
        }
        return address;
    }

    public boolean hasPort() {
        return port > 0;
    }

    private static boolean isValidPort(Integer port) {
        return port == null || (port >= 0 && port <= 65535);
    }

    private static final String ADDRESS_PATTERN_STR = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
            "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])" +
            "(:([0-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?$";

    private static final String IP_PATTERN_STR = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
            "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

    public static final Pattern ADDRESS_PATTERN = Pattern.compile(ADDRESS_PATTERN_STR);

    public static final Pattern IP_PATTERN = Pattern.compile(IP_PATTERN_STR);

    public static boolean isLegalIp(String ip) {
        return IP_PATTERN.matcher(ip).find();
    }

    public static long ipToLong(String ip) {
        long[] ipParts = new long[4];

        int pos1 = ip.indexOf(".");
        int pos2 = ip.indexOf(".", pos1 + 1);
        int pos3 = ip.indexOf(".", pos2 + 1);

        ipParts[0] = Long.parseLong(ip.substring(0, pos1));
        ipParts[1] = Long.parseLong(ip.substring(pos1 + 1, pos2));
        ipParts[2] = Long.parseLong(ip.substring(pos2 + 1, pos3));
        ipParts[3] = Long.parseLong(ip.substring(pos3 + 1));

        return (ipParts[0] << 24) + (ipParts[1] << 16) + (ipParts[2] << 8) + ipParts[3];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IpAndPort)) return false;

        IpAndPort ipAndPort = (IpAndPort) o;

        if (port != ipAndPort.port) return false;
        if (!ip.equals(ipAndPort.ip)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "IpAndPort{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
