package com.qunar.corp.cactus.service.impl;

import com.qunar.corp.cactus.monitor.SystemCounter;
import com.qunar.corp.cactus.service.NodeRelationService;
import com.qunar.corp.cactus.service.SpecialService;
import com.qunar.corp.cactus.service.graph.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.concurrent.NamedThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2015 2015/3/3 16:25
 */
public class NodeRelationServiceImpl implements NodeRelationService {

    private static final Logger logger = LoggerFactory.getLogger(NodeRelationServiceImpl.class);

    @Resource
    private GraphService graphService;

    @Resource
    private SpecialService specialService;

    private AtomicBoolean canSubmit = new AtomicBoolean(true);

    private ExecutorService executorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("node-relation"));

    @PostConstruct
    public void init() {
        logger.info("first init specials");
        try {
            specialService.freshSpecials();
        } catch (RuntimeException e) {
            logger.error("occur error when first init special groups", e);
            throw e;
        }
        logger.info("first init specials over");

        logger.info("start build graph after init");
        try {
            graphService.freshGraph();
        } catch (RuntimeException e) {
            logger.error("occur error when first build graph");
            throw e;
        }
        logger.info("build graph success after init");

        Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("fresh-timer")).scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                fresh();
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void fresh() {
        logger.info("start fresh node relation");
        if (canSubmit.compareAndSet(true, false)) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    canSubmit.set(true);

                    try {
                        specialService.freshSpecials();
                    } catch (Exception e) {
                        SystemCounter.failedSpecialInit.increment();
                        logger.error("occur error when init special groups", e);
                    }

                    try {
                        graphService.freshGraph();
                    } catch (Exception e) {
                        SystemCounter.failedGraphInit.increment();
                        logger.error("execute buildGraph error", e);
                    }
                }
            });
        } else {
            logger.info("need not fresh node relation");
        }
    }
}
