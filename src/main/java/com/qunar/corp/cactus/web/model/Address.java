package com.qunar.corp.cactus.web.model;

/**
 * @author zhenyu.nie created on 2014 2014/9/16 11:34
 */
public class Address {

    private final String ip;

    private final String hostname;

    private final int port;

    public Address(String ip, String hostname, int port) {
        this.ip = ip;
        this.hostname = hostname;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}
