package com.qunar.corp.cactus.drainage.bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * @author sen.chai
 * @date 2015-04-21 14:23
 */
public class DrainageParam implements Serializable {

	private static final long serialVersionUID = 8346635703736931380L;
	private String appCode;
	private String serviceName;
	private String methodName;

	private Set<DrainageIpAndPort> serviceIpAndPort;
	private Set<DrainageGroup> targetGroups;
	
	private static  final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
	

	public DrainageParam() {
		super();
	}
	
	public DrainageParam(String serviceName, String methodName) {
		super();
		this.serviceName = serviceName;
		this.methodName = methodName;
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

	public Set<DrainageIpAndPort> getServiceIpAndPort() {
		return serviceIpAndPort;
	}

	public void setServiceIpAndPort(Set<DrainageIpAndPort> serviceIpAndPort) {
		this.serviceIpAndPort = serviceIpAndPort;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public Set<DrainageGroup> getTargetGroups() {
		return targetGroups;
	}

	public void setTargetGroups(Set<DrainageGroup> targetGroups) {
		this.targetGroups = targetGroups;
	}
	
	public List<String>  parseMethodNames(){
		if(Strings.isNullOrEmpty(methodName)){
			return Collections.EMPTY_LIST;
		}else{
			return SPLITTER.splitToList(methodName);
		}
	}

	@Override
	public String toString() {
		return "DrainageParam{" + "appCode='" + appCode + '\'' + ", serviceName='" + serviceName + '\'' + ", methodName='" + methodName + '\'' + ", serviceIpAndPort=" + serviceIpAndPort
				+ ", targetGroups=" + targetGroups + '}';
	}
}
