/**
 * Project: dubbo.registry-1.1.0-SNAPSHOT
 * 
 * File Created at 2010-4-9
 * $Id: Provider.java 182846 2012-06-28 09:37:59Z tony.chenl $
 * 
 * Copyright 2008 Alibaba.com Croporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.qunar.corp.cactus.web.model;

import java.util.Date;
import java.util.Map;

/**
 * Provider
 * 
 * @author william.liangf
 * @author tony.chenl
 */
public class Provider{

    private static final long serialVersionUID = 5981342400350878171L;

    private long id;

    private String group;/*zk上的group节点名称，注意：这个值可能为空，因为仅仅会在搜索时设置这个值*/

    private String serviceGroup; /*service的group属性，可能为空*/

    private String serviceKey;/* 提供者的servicekey */
    
    private String url; /* 提供者提供服务的地址 */

    private String encodeUrl;/*编码过后的url，用于页面传参*/

    private String address; /* 提供者地址 */

    private String hostNameAndPort;/*机器名*/

    private String registry;/* 提供者连接的注册中心地址 */
    
    private boolean dynamic;          /* 是否为动态注册服务 */
    
    private boolean enabled;          /* 是否启用 */

    private int weight;          /* 权重 */

	private String application; /* 应用名 */

    private String username;      /* 提供者用户名 */
    
    private Date expired;   /*过期时间*/
    
    private long alived;    /*存活时间，单位秒*/

    private String version; /*接口版本号*/

    private String methods; /*方法名*/

    private String intefaze; /*接口名（等价于service节点的名称）*/

    private String pid; /*进程号*/

    private String dubboVersion; /*dubbo版本号*/
    
    private Map<String,String> params;

    public Provider() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEncodeUrl() {
        return encodeUrl;
    }

    public void setEncodeUrl(String encodeUrl) {
        this.encodeUrl = encodeUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHostNameAndPort() {
        return hostNameAndPort;
    }

    public void setHostNameAndPort(String hostNameAndPort) {
        this.hostNameAndPort = hostNameAndPort;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    
    public Date getExpired() {
        return expired;
    }

    
    public void setExpired(Date expired) {
        this.expired = expired;
    }
    
    public long getAlived() {
        return alived;
    }

    public void setAlived(long aliveSeconds) {
        this.alived = aliveSeconds;
    }

    public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIntefaze() {
        return intefaze;
    }

    public void setIntefaze(String intefaze) {
        this.intefaze = intefaze;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDubboVersion() {
        return dubboVersion;
    }

    public void setDubboVersion(String dubboVersion) {
        this.dubboVersion = dubboVersion;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Provider provider = (Provider) o;

        if (!url.equals(provider.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
