package com.qunar.corp.cactus.drainage.bean;

import java.io.Serializable;

/**
 * @author sen.chai
 * @date 2015-04-21 21:55
 */
public class TcpcopyParam implements Serializable {

    private String serviceIp;
    private String proxyIp;
    private int servicePort;
    private int proxyPort;
    private String interceptIp;
    private String routeIp;
    private RunningStatus status;

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getInterceptIp() {
        return interceptIp;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public void setInterceptIp(String interceptIp) {
        this.interceptIp = interceptIp;
    }

    public RunningStatus getStatus() {
        return status;
    }

    public void setStatus(RunningStatus status) {
        this.status = status;
    }

    public String getRouteIp() {
        return routeIp;
    }

    public void setRouteIp(String routeIp) {
        this.routeIp = routeIp;
    }
}
