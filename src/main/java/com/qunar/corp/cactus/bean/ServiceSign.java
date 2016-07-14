/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Preconditions;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.UrlHelper;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;


/**
 * @author zhenyu.nie created on 2013 13-11-6 下午5:35
 */
// 用来唯一标识一个provider服务
public class ServiceSign {

    public final long zkId;
    public final String group;
    public final IpAndPort ipAndPort;
    public final String serviceGroup;
    public final String serviceInterface;
    public final String version;

    private ServiceSign(long zkId, String group, String address, String serviceGroup, String serviceInterface, String version) {
        group = nullToEmpty(group).trim();
        if (group.startsWith("/")) {
            group = group.substring(1);
        }

        address = nullToEmpty(address).trim();
        serviceGroup = nullToEmpty(serviceGroup).trim();
        serviceInterface = nullToEmpty(serviceInterface).trim();
        version = nullToEmpty(version).trim();

        this.group = group;
        if (isNullOrEmpty(address)) {
            ipAndPort = null;
        } else {
            this.ipAndPort = IpAndPort.fromString(address);
        }
        this.serviceGroup = serviceGroup;
        this.serviceInterface = serviceInterface;
        this.version = version;
        this.zkId = zkId;
        check();
    }

    private void check() {
        Preconditions.checkArgument(!isNullOrEmpty(group), "group should not be empty or null");
        Preconditions.checkArgument(!isNullOrEmpty(serviceInterface), "serviceInterface should not be empty or null");
    }

    public long getZkId() {
        return zkId;
    }

    public String getGroup() {
        return group;
    }

    public boolean hasAddress() {
        return ipAndPort != null;
    }

    public String getAddress() {
        if (ipAndPort == null) {
            return null;
        } else {
            return ipAndPort.getAddress();
        }
    }

    public String getIp() {
        if (ipAndPort == null) {
            return null;
        } else {
            return ipAndPort.getIp();
        }
    }

    public int getPort() {
        if (ipAndPort == null) {
            return 0;
        } else {
            return ipAndPort.getPort();
        }
    }

    public boolean hasPort() {
        return getPort() > 0;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public String getVersion() {
        return version;
    }

    public static ServiceSign makeServiceSign(long zkId, String group, String address,
                                              String serviceGroup, String serviceInterface, String version) {
        return new ServiceSign(zkId, group, address, serviceGroup, serviceInterface, version);
    }

    public static ServiceSign makeServiceSign(long zkId, String group, String address, String serviceKey) {
        serviceKey = nullToEmpty(serviceKey).trim();
        String serviceGroup;
        String version;
        String serviceInterface;

        int indexOfSlash = serviceKey.indexOf('/');
        if (indexOfSlash != -1) {
            serviceGroup = serviceKey.substring(0, indexOfSlash).trim();
            serviceKey = serviceKey.substring(indexOfSlash + 1);
        } else {
            serviceGroup = "";
        }

        int indexOfColon = serviceKey.indexOf(':');
        if (indexOfColon != -1) {
            version = serviceKey.substring(indexOfColon + 1).trim();
            serviceKey = serviceKey.substring(0, indexOfColon);
        } else {
            version = "";
        }
        serviceInterface = serviceKey.trim();

        return new ServiceSign(zkId, group, address, serviceGroup, serviceInterface, version);
    }

    public static ServiceSign makeServiceSign(long zkId, String group, String serviceKey) {
        return ServiceSign.makeServiceSign(zkId, group, null, serviceKey);
    }

    public static ServiceSign makeServiceSign(URL url) {
        String group = UrlHelper.getGroup(url);
        return makeServiceSign(Long.parseLong(url.getParameter(ConstantHelper.ZKID)), group, url.getAddress(), url.getServiceKey());
    }

    public String getServiceKey() {
        StringBuilder sb = new StringBuilder();
        if (!serviceGroup.isEmpty()) {
            sb.append(serviceGroup);
            sb.append("/");
        }
        sb.append(serviceInterface);
        if (!version.isEmpty()) {
            sb.append(":");
            sb.append(version);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceSign)) return false;

        ServiceSign that = (ServiceSign) o;

        if (zkId != that.zkId) return false;
        if (group != null ? !group.equals(that.group) : that.group != null) return false;
        if (ipAndPort != null ? !ipAndPort.equals(that.ipAndPort) : that.ipAndPort != null) return false;
        if (serviceGroup != null ? !serviceGroup.equals(that.serviceGroup) : that.serviceGroup != null) return false;
        if (serviceInterface != null ? !serviceInterface.equals(that.serviceInterface) : that.serviceInterface != null)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (zkId ^ (zkId >>> 32));
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (ipAndPort != null ? ipAndPort.hashCode() : 0);
        result = 31 * result + (serviceGroup != null ? serviceGroup.hashCode() : 0);
        result = 31 * result + (serviceInterface != null ? serviceInterface.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServiceSign{" +
                "zkId=" + zkId +
                ", group='" + group + '\'' +
                ", ipAndPort=" + ipAndPort +
                ", serviceGroup='" + serviceGroup + '\'' +
                ", serviceInterface='" + serviceInterface + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
