package com.qunar.corp.cactus.bean;

import java.util.Date;

/**
 * Date: 13-11-6
 * Time: 下午5:57
 *
 * @author: xiao.liang
 * @description:
 */
public class Log {

    private int id;
    private User user;
    private ZKCluster zkCluster;
    private String group;
    private String serviceGroup;
    private String service;
    private String version;
    private String hostName;//格式为机器名：端口号
    private String ip;
    private int port;
    private String message;
    private Date operateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ZKCluster getZkCluster() {
        return zkCluster;
    }

    public void setZkCluster(ZKCluster zkCluster) {
        this.zkCluster = zkCluster;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", user=" + user +
                ", zkCluster=" + zkCluster +
                ", group='" + group + '\'' +
                ", serviceGroup='" + serviceGroup + '\'' +
                ", service='" + service + '\'' +
                ", version='" + version + '\'' +
                ", hostName='" + hostName + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", message='" + message + '\'' +
                ", operateTime=" + operateTime +
                '}';
    }
}
