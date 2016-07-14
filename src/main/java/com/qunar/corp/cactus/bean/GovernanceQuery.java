/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.alibaba.dubbo.common.URL;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午2:23
 *
 */
public class GovernanceQuery extends HashMap<String, Object> {

    private GovernanceQuery(Map<String, Object> params) {
        super(params);
    }

    @Override
    public Object put(String key, Object value){
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;

        private Long zkId;

        private String name;

        private String group;

        private String ip;

        private Integer port;

        private String serviceGroup;

        private String serviceName;

        private String version;

        private String urlStr;

        //override provider consumer or router
        private PathType pathType;

        private String lastOperator;

        //delete online offline
        private Status status;

        //zkdata or userdata
        private DataType dataType;

        private DataType notDataType;

        private Timestamp createTime;

        private Timestamp lastUpdateTime;

        private Timestamp fromUpdateTime;

        private Timestamp toUpdateTime;

        private String likeName;

        private String likeGroup;

        private String likeServiceName;

        private Status notStatus;

        private GovernanceQuery otherGovernanceQuery;
        
        private Set<String> toRemoveKeys = Sets.newHashSet();

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder zkId(long zkId) {
            this.zkId = zkId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder group(String group) {
            this.group = group;
            return this;
        }

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder serviceGroup(String serviceGroup) {
            this.serviceGroup = serviceGroup;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder url(String urlStr) {
            this.urlStr = urlStr;
            return this;
        }

        public Builder url(URL url) {
            this.urlStr = url.toFullString();
            return this;
        }

        public Builder pathType(PathType pathType) {
            this.pathType = pathType;
            return this;
        }

        public Builder lastOperator(String lastOperator) {
            this.lastOperator = lastOperator;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder dataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder notDataType(DataType notDataType) {
            this.notDataType = notDataType;
            return this;
        }

        public Builder createTime(Timestamp createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder lastUpdateTime(Timestamp lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
            return this;
        }

        public Builder fromUpdateTime(Timestamp fromUpdateTime) {
            this.fromUpdateTime = fromUpdateTime;
            return this;
        }

        public Builder toUpdateTime(Timestamp toUpdateTime) {
            this.toUpdateTime = toUpdateTime;
            return this;
        }

        public Builder likeName(String likeName) {
            this.likeName = likeName;
            return this;
        }

        public Builder likeGroup(String likeGroup) {
            this.likeGroup = likeGroup;
            return this;
        }

        public Builder likeServcieName(String likeServiceName) {
            this.likeServiceName = likeServiceName;
            return this;
        }

        public Builder notStatus(Status notStatus) {
            this.notStatus = notStatus;
            return this;
        }

        public Builder fromServiceSign(ServiceSign sign) {
            this.zkId = sign.zkId;
            this.group = sign.group;
            this.ip = sign.getIp();
            this.port = sign.getPort();
            this.serviceName = sign.serviceInterface;
            this.serviceGroup = sign.serviceGroup;
            this.version = sign.version;
            return this;
        }

        public Builder fromGroupAndServiceKey(ServiceSign sign) {
            this.group = sign.group;
            this.serviceName = sign.serviceInterface;
            this.serviceGroup = sign.serviceGroup;
            this.version = sign.version;
            return this;
        }

        public Builder fromGovernanceQuery(GovernanceQuery governanceQuery){
            this.otherGovernanceQuery = governanceQuery;
            return this;
        }

        public Builder removeId() {
            toRemoveKeys.add("id");
            return this;
        }

        public Builder removeZkId() {
            toRemoveKeys.add("zkId");
            return this;
        }

        public Builder removeName() {
            toRemoveKeys.add("name");
            return this;
        }

        public Builder removeGroup() {
            toRemoveKeys.add("group");
            return this;
        }

        public Builder removeIp() {
            toRemoveKeys.add("ip");
            return this;
        }

        public Builder removePort() {
            toRemoveKeys.add("port");
            return this;
        }

        public Builder removeServiceGroup() {
            toRemoveKeys.add("serviceGroup");
            return this;
        }

        public Builder removeServiceName() {
            toRemoveKeys.add("serviceName");
            return this;
        }

        public Builder removeVersion() {
            toRemoveKeys.add("version");
            return this;
        }

        public Builder removeUrl() {
            toRemoveKeys.add("url");
            return this;
        }

        public Builder removePathType() {
            toRemoveKeys.add("pathType");
            return this;
        }

        public Builder removeLastOperator() {
            toRemoveKeys.add("lastOperator");
            return this;
        }

        public Builder removeStatus() {
            toRemoveKeys.add("status");
            return this;
        }

        public Builder removeDataType() {
            toRemoveKeys.add("dataType");
            return this;
        }

        public Builder removeNotDataType() {
            toRemoveKeys.add("notDataType");
            return this;
        }

        public Builder removeCreateTime() {
            toRemoveKeys.add("createTime");
            return this;
        }

        public Builder removeLastUpdateTime() {
            toRemoveKeys.add("lastUpdateTime");
            return this;
        }

        public Builder removeFromUpdateTime() {
            toRemoveKeys.add("fromUpdateTime");
            return this;
        }

        public Builder removeToUpdateTime() {
            toRemoveKeys.add("toUpdateTime");
            return this;
        }

        public Builder removeLikeName() {
            toRemoveKeys.add("likeName");
            return this;
        }

        public Builder removeLikeGroup() {
            toRemoveKeys.add("likeGroup");
            return this;
        }

        public Builder removeLikeServcieName() {
            toRemoveKeys.add("likeServiceName");
            return this;
        }

        public Builder removeNotStatus() {
            toRemoveKeys.add("notStatus");
            return this;
        }

        public GovernanceQuery build() {
            Map<String, Object> params = Maps.newHashMap();
            if (otherGovernanceQuery != null){
                params.putAll(otherGovernanceQuery); 
            }
            if (id != null) {
                params.put("id", id);
            }
            if (zkId != null) {
                params.put("zkId", zkId);
            }
            if (name != null) {
                params.put("name", name);
            }
            if (group != null) {
                params.put("group", group);
            }
            if (ip != null) {
                params.put("ip", ip);
            }
            if (port != null) {
                params.put("port", port);
            }
            if (serviceGroup != null && !serviceGroup.isEmpty()) {
                params.put("serviceGroup", serviceGroup);
            }
            if (serviceName != null) {
                params.put("serviceName", serviceName);
            }
            if (version != null && !version.isEmpty()) {
                params.put("version", version);
            }
            if (urlStr != null) {
                params.put("urlStr", urlStr);
            }
            if (pathType != null) {
                params.put("pathType", pathType.getCode());
            }
            if (lastOperator != null) {
                params.put("lastOperator", lastOperator);
            }
            if (status != null) {
                params.put("status", status.getCode());
            }
            if (dataType != null) {
                params.put("dataType", dataType.getCode());
            }
            if (notDataType != null) {
                params.put("notDataType", notDataType.getCode());
            }
            if (createTime != null) {
                params.put("createTime", createTime);
            }
            if (lastUpdateTime != null) {
                params.put("lastUpdateTime", lastUpdateTime);
            }
            if (fromUpdateTime != null) {
                params.put("fromUpdateTime", fromUpdateTime);
            }
            if (toUpdateTime != null) {
                params.put("toUpdateTime", toUpdateTime);
            }
            if (likeName != null) {
                params.put("likeName", likeName);
            }
            if (likeGroup != null) {
                params.put("likeGroup", likeGroup);
            }
            if (likeServiceName != null) {
                params.put("likeServiceName", likeServiceName);
            }
            if (notStatus != null) {
                params.put("notStatus", notStatus.getCode());
            }
            for (String key : toRemoveKeys) {
                params.remove(key);
            }
            return new GovernanceQuery(params);
        }
    }
}
