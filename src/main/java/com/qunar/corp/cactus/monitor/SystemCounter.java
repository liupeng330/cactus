/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.monitor;

import com.qunar.base.meerkat.monitor.AtomicLongCounter;
import com.qunar.base.meerkat.monitor.QunarMonitor;

/**
 * @author zhenyu.nie created on 2014 14-2-12 下午8:54
 */
public class SystemCounter {

    private static final String ZK_FAILED_OPERATION = "tc.cactus.zk_failed_operation";

    private static final String ZK_CHANGE_OPERATION = "tc.cactus.zk_change_operation";

    private static final String API_CALL_OPERATION = "tc.cactus.api_call_operation";

    private static final String API_CALL_FAILED_OPERATION = "tc.cactus.api_call_failed_operation";

    private static final String API_CALL_ZK_NOT_FIND = "tc.cactus.api_call_zk_not_find";

    private static final String FAILED_SPECIAL_INIT = "tc.cactus.failed_special_init";

    private static final String FAILED_GRAPH_INIT = "tc.cactus.failed_graph_init";

    private static final String MOCK_CONSUMER_COUNT = "tc.cactus.mock_consumer_count";

    private static final String BUILD_NEW_MOCK_REF = "tc.cactus.build_new_mock_ref";

    public static final AtomicLongCounter failedSpecialInit = QunarMonitor
            .createAtomicLongCounter(FAILED_SPECIAL_INIT, "失败的监控路径初始化");

    public static final AtomicLongCounter failedGraphInit = QunarMonitor
            .createAtomicLongCounter(FAILED_GRAPH_INIT, "失败的图初始化");

    public static final AtomicLongCounter zkFailedOperation = QunarMonitor
            .createAtomicLongCounter(ZK_FAILED_OPERATION, "失败的操作数");

    public static final AtomicLongCounter zkChangeOperation = QunarMonitor
            .createAtomicLongCounter(ZK_CHANGE_OPERATION, "zk写入数");

    public static final AtomicLongCounter apiCallOperation = QunarMonitor
            .createAtomicLongCounter(API_CALL_OPERATION, "api调用数");

    public static final AtomicLongCounter apiCallFailedOperation = QunarMonitor
            .createAtomicLongCounter(API_CALL_FAILED_OPERATION, "api调用失败数");

    public static final AtomicLongCounter apiCallZkNotFind = QunarMonitor
            .createAtomicLongCounter(API_CALL_ZK_NOT_FIND, "api调用找不到zk数");

    public static final AtomicLongCounter mockConsumerCount = QunarMonitor
            .createAtomicLongCounter(MOCK_CONSUMER_COUNT, "consumer mock调用数");

    public static final AtomicLongCounter buildNewMockRef = QunarMonitor
            .createAtomicLongCounter(BUILD_NEW_MOCK_REF, "生成新的mock引用数");

    static {
        QunarMonitor.addPeriodMonitor(mockConsumerCount);
        QunarMonitor.addPeriodMonitor(failedSpecialInit);
        QunarMonitor.addPeriodMonitor(zkFailedOperation);
        QunarMonitor.addPeriodMonitor(zkChangeOperation);
        QunarMonitor.addPeriodMonitor(apiCallOperation);
        QunarMonitor.addPeriodMonitor(apiCallFailedOperation);
        QunarMonitor.addPeriodMonitor(apiCallZkNotFind);
        QunarMonitor.addPeriodMonitor(buildNewMockRef);
    }
}
