package com.qunar.corp.cactus.drainage.bean;

import java.sql.Timestamp;

/**
 * @author sen.chai
 * @date 2015-05-11 10:19
 */
public class DrainageServiceInfo {

    private Long id;

    private String drainageKey;

    private String serviceName;

    private String methodName;

    private String serviceIp;

    private int servicePort;

    private Timestamp createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDrainageKey() {
        return drainageKey;
    }

    public void setDrainageKey(String drainageKey) {
        this.drainageKey = drainageKey;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }
}
