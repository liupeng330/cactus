/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.Status;
import com.qunar.corp.cactus.event.EventType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.alibaba.dubbo.common.Constants.*;

/**
 * @author zhenyu.nie created on 2013 13-11-5 下午12:29
 */
public class ConstantHelper {

    public static final Map<Status, EventType> STATUS_EVENT_TYPE_MAP;

    public static final Map<EventType, Status> EVENT_STATUS_MAP;

    static {
        EVENT_STATUS_MAP = Maps.newHashMapWithExpectedSize(4);
        EVENT_STATUS_MAP.put(EventType.NEW, Status.ENABLE);
        EVENT_STATUS_MAP.put(EventType.ENABLE, Status.ENABLE);
        EVENT_STATUS_MAP.put(EventType.DISABLE, Status.DISABLE);
        EVENT_STATUS_MAP.put(EventType.DELETE, Status.DEL);

        STATUS_EVENT_TYPE_MAP = Maps.newHashMapWithExpectedSize(3);
        STATUS_EVENT_TYPE_MAP.put(Status.ENABLE, EventType.ENABLE);
        STATUS_EVENT_TYPE_MAP.put(Status.DISABLE, EventType.DISABLE);
        STATUS_EVENT_TYPE_MAP.put(Status.DEL, EventType.DELETE);
    }

    // dubbo的Constants里面本身有个REGISTRY_KEY参数
    public static final String Q_REGISTRY_KEY = "registryKey";

    public static final String BACK_URL_PARAM_SPLITTER = "|~";

    public static final String USERNAME_COOKIE_NAME = "cactus_username";

    public static final String UID_COOKIE_NAME = "cactus_uid";

    public static final int INVALID_UID = 0;

    public static final int UNLOGIN_UID = -1;

    public static final String NEW_LINE = System.getProperty("line.separator");

    // 若用户在consumer中设置了默认的mock值，则使用动态配置来对其进行容错、屏蔽操作会不起效果，
    // 故要在获取的consumer URL中需要设置以下参数来区分其默认设置的mock值和configurator中设置的mock值
    public static final String MOCK_KEY_ON_CONFIG_URL = "newMockState";

    // 当url中不存在Q_REGISTRY_KEY时，若需要在url中存储group的值，则可以添加如下参数
    public static final String FAKE_Q_REGISTRY_KEY = "fakeRegistryKey";

    public static final String OWNER = "owner";

    public static final String ROOT_PATH = "/";

    public static final String ZKID = "zkid";

    public static final String ZK_NAME = "zkName";

    public static final String CACTUS = "cactus";

    public static final String CACTUS_VERSION = "1.5.0";

    public static final String CACTUS_TIME = "cactusTime";

    public static final List<String> ORIGIN_LOAD_BALANCE_VALUES =
            Arrays.asList("random", "roundrobin", "leastactive", "consistenthash");

    public static final String NAME = "name";

    public static final String STATUS = "status";

    public static final String DATA_TYPE = "dataType";

    public static final String USER_DATA_ID = "userDataId";

    public static final int WEIGHT_TYPE_HALF = 0;

    public static final int WEIGHT_TYPE_DOUBLE = 1;

    public static final Set<String> CONDITION_KEYS = ImmutableSet.of(CATEGORY_KEY, DYNAMIC_KEY, ENABLED_KEY,
            APPLICATION_KEY, ANYHOST_KEY, RUNTIME_KEY, FORCE_KEY, TIMESTAMP_KEY, SIDE_KEY, Q_REGISTRY_KEY, GROUP_KEY,
            VERSION_KEY, CLASSIFIER_KEY, CACTUS, CACTUS_TIME, ZKID, STATUS, DATA_TYPE, NAME, FAKE_Q_REGISTRY_KEY);

    public static final String CACTUS_API = "cactus.api";

    public static final int CACTUS_API_ID = 10000;

    public static final String CACTUS_USER_NAME = "cactusUserName";

    public static final String CACTUS_USER_ID = "cactusUserId";

    public static final String CACTUS_LOG_NEW_DATA = "cactusLogNewData";

    public static final long EMPTY_ID = -1;

    public static final int SERVICE_SEARCH_TYPE = 0;

    public static final int GROUP_SEARCH_TYPE = 1;

    public static final int MACHINE_SEARCH_TYPE = 2;

    public static final String CACTUS_ONLINE_STATUS = "cactusOnlineStatus";

    public static final String PROVIDERS = "providers";
    public static final String CONSUMERS = "consumers";
    public static final String ROUTERS = "routers";
    public static final String CONFIGURATORS = "configurators";

    public static final Set<String> DUBBO_NODES = ImmutableSet.of(PROVIDERS, CONSUMERS, ROUTERS, CONFIGURATORS);
}
