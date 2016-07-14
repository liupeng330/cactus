/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.bean;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午2:23
 * 
 */
public class LogQuery extends HashMap<String, Object> {

    private LogQuery(Map<String, Object> params) {
        super(params);
    }

    @Override
    public Object put(String key, Object value) {
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

    public static Builder makeBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;

        private String username;

        private String group;

        private String service;

        private String hostName;

        private String likeUsername;

        private String likeHostName;

        private String likeGroup;

        private String likeService;

        private LogQuery otherLogQuery;

        private Date fromTime;

        private Date toTime;

        private Set<String> toRemoveKeys = Sets.newHashSet();

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder group(String group) {
            this.group = group;
            return this;
        }

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public Builder hostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public Builder likeUsername(String likeUsername) {
            this.likeUsername = likeUsername;
            return this;
        }

        public Builder likeHostName(String likeHostName) {
            this.likeHostName = likeHostName;
            return this;
        }

        public Builder likeGroup(String likeGroup) {
            this.likeGroup = likeGroup;
            return this;
        }

        public Builder likeService(String likeService) {
            this.likeService = likeService;
            return this;
        }

        public Builder fromTime(Date fromTime) {
            this.fromTime = fromTime;
            return this;
        }

        public Builder toTime(Date toTime) {
            this.toTime = toTime;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fromLogQuery(LogQuery logQuery) {
            this.otherLogQuery = logQuery;
            return this;
        }

        public Builder removeId() {
            toRemoveKeys.add("id");
            return this;
        }

        public Builder removeGroup() {
            toRemoveKeys.add("group");
            return this;
        }

        public Builder removeService() {
            toRemoveKeys.add("service");
            return this;
        }

        public Builder removeUsername() {
            toRemoveKeys.add("username");
            return this;
        }

        public Builder removeHostName() {
            toRemoveKeys.add("hostName");
            return this;
        }

        public Builder removeLikeGroup() {
            toRemoveKeys.add("likeGroup");
            return this;
        }

        public Builder removeLikeService() {
            toRemoveKeys.add("likeService");
            return this;
        }

        public Builder removeLikeHostName() {
            toRemoveKeys.add("likeHostName");
            return this;
        }

        public Builder removeLikeUsername() {
            toRemoveKeys.add("likeUsername");
            return this;
        }

        public Builder removeFromTime() {
            toRemoveKeys.add("fromTime");
            return this;
        }

        public Builder removeToTime() {
            toRemoveKeys.add("toTime");
            return this;
        }

        public LogQuery build() {
            Map<String, Object> params = Maps.newHashMap();
            if (otherLogQuery != null) {
                params.putAll(otherLogQuery);
            }
            if (id != null) {
                params.put("id", id);
            }
            if (username != null) {
                params.put("username", username);
            }
            if (group != null) {
                params.put("group", group);
            }
            if (service != null) {
                params.put("service", service);
            }
            if (hostName != null) {
                params.put("hostName", hostName);
            }
            if (likeUsername != null) {
                params.put("likeUsername",likeUsername);
            }
            if (likeGroup != null) {
                params.put("likeGroup", likeGroup);
            }
            if (likeService != null) {
                params.put("likeService", likeService);
            }
            if (likeHostName != null) {
                params.put("likeHostName", likeHostName);
            }
            if (fromTime != null) {
                params.put("fromTime", fromTime);
            }
            if (toTime != null) {
                params.put("toTime", toTime);
            }
            for (String key : toRemoveKeys) {
                params.remove(key);
            }
            return new LogQuery(params);
        }
    }
}
