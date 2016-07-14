/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.event.impl;

import com.alibaba.dubbo.common.URL;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.Log;
import com.qunar.corp.cactus.bean.User;
import com.qunar.corp.cactus.dao.LogDao;
import com.qunar.corp.cactus.event.EventListener;
import com.qunar.corp.cactus.event.EventType;
import com.qunar.corp.cactus.event.UrlChangeEvent;
import com.qunar.corp.cactus.util.CommonCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

import static com.qunar.corp.cactus.util.ConstantHelper.CACTUS_LOG_NEW_DATA;

/**
 * @author zhenyu.nie created on 2013 13-12-27 下午3:56
 */
@Service
public class UrlChangeLogger implements EventListener {

    @Resource
    private LogDao logDao;

    final String HOSTNAME_PATTERN = "%s%s(%s)";

    private static final Map<EventType, String> pattern = Maps.newHashMapWithExpectedSize(4);

    static {
        pattern.put(EventType.NEW, "新增url：{%s}");
        pattern.put(EventType.ENABLE, "启用url：{%s}");
        pattern.put(EventType.DISABLE, "禁用url：{%s}");
        pattern.put(EventType.DELETE, "删除url：{%s}");
        pattern.put(EventType.CHANGE, "修改url：{%s}");
    }

    @Subscribe
    public void log(UrlChangeEvent changeEvent) {
        logDao.add(makeLog(changeEvent));
    }

    private Log makeLog(UrlChangeEvent changeEvent) {
        Log log = new Log();
        GovernanceData data = changeEvent.getData().copy();
        log.setGroup(data.getGroup());
        log.setService(data.getServiceName());
        URL url = data.getFullUrl();
        log.setHostName(String.format(HOSTNAME_PATTERN, CommonCache.getHostNameByIp(url.getIp()),
                url.getPort() == 0 ? "" : ":" + url.getPort(), url.getAddress()));
        setMessage(changeEvent, log, data);
        log.setOperateTime(new Date());
        User user = new User();
        user.setId(changeEvent.uid);
        log.setUser(user);
        return log;
    }

    private void setMessage(UrlChangeEvent changeEvent, Log log, GovernanceData data) {
        if (changeEvent.type == EventType.CHANGE) {
            log.setMessage(String.format(pattern.get(changeEvent.type),
                    data.getUrl().removeParameter(CACTUS_LOG_NEW_DATA),
                    URL.decode(data.getUrl().getParameter(CACTUS_LOG_NEW_DATA))));
        } else {
            log.setMessage(String.format(pattern.get(changeEvent.type), data.getUrlStr()));
        }
    }
}
