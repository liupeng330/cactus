package com.qunar.corp.cactus.drainage.service;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sen.chai
 * @date 2015-05-12 21:11
 */
public class RetryInvoker {

    private static final Logger logger = LoggerFactory.getLogger(RetryInvoker.class);

    public int retryCount = 3;

    public long retryInterval = 50;

    public boolean enableRetryInterval = true;

    public RetryInvoker() {
    }

    public RetryInvoker(int retryCount, long retryInterval) {
        this.retryCount = retryCount;
        this.retryInterval = retryInterval;
    }

    public RetryInvoker(int retryCount, long retryInterval, boolean enableRetryInterval) {
        this.retryCount = retryCount;
        this.retryInterval = retryInterval;
        this.enableRetryInterval = enableRetryInterval;
    }

    public static RetryInvoker of(int retryCount, long retryInterval) {
        return new RetryInvoker(retryCount, retryInterval);
    }

    public static RetryInvoker of(int retryCount, long retryInterval, boolean enableRetryInterval) {
        return new RetryInvoker(retryCount, retryInterval, enableRetryInterval);
    }

    public void invoke(VoidInvokeHolder invokeHolder) {
        for (int i = 0; i < retryCount; i++) {
            try {
                logger.info("{} time invoke begin", i + 1);
                invokeHolder.invoke();
                logger.info("{} time invoke success", i + 1);
                break;
            } catch (Exception e) {
                logger.info("{} time invoke failed!", i + 1);
                if (i == retryCount - 1) {
                    throw Throwables.propagate(e);
                }
            }
            if (enableRetryInterval) {
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    logger.error("", e);
                    throw Throwables.propagate(e);
                }
            }
        }
    }

    public static interface VoidInvokeHolder {
        void invoke();
    }


    public RetryInvoker disableRetryInterval() {
        this.enableRetryInterval = false;
        return this;
    }

    public RetryInvoker retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

}
