package com.qunar.corp.cactus.zk;

import com.qunar.corp.cactus.util.PathUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-10-28
 * Time: 下午5:55
 */
public class ZKServiceImpl implements ZKService {

    private static Logger logger = LoggerFactory.getLogger(ZKServiceImpl.class);

    private CuratorFramework curatorFramework;

    private final String address;

    public ZKServiceImpl(final String address) {
        this.address = address;
        init();
    }

    private void init() {
        final long beginTime = System.currentTimeMillis();
        logger.info("begin init zk cluster, address is {}", address);
        curatorFramework = CuratorFrameworkFactory.newClient(address, new ExponentialBackoffRetry(2000, 3));
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState == ConnectionState.CONNECTED) {
                    countDownLatch.countDown();
                } else if (connectionState == ConnectionState.LOST) {
                    reconnect();
                }
            }
        });
        curatorFramework.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("wait zk client start error", e);
            throw new RuntimeException("wait zk client start error");
        }
        logger.info("end init zk cluster, address is {} and time is {}", address, System.currentTimeMillis() - beginTime);
    }

    private void reconnect() {
        close();
        init();
    }

    @Override
    public List<String> getPathChildren(String path) throws Exception {
        path = PathUtil.ensureStartWithSlash(path);

        return curatorFramework.getChildren().forPath(path);
    }

    public void close() {
        if (curatorFramework != null) {
            try {
                curatorFramework.close();
            } catch (Throwable e) {
                logger.error("occur error when close curator framework");
            }
        }
    }

    @Override
    public void register(String path, boolean ephemeral) throws Exception {
        path = PathUtil.ensureStartWithSlash(path);
        CreateMode mode = ephemeral ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT;
        curatorFramework.create().withMode(mode).forPath(path);
    }

    @Override
    public void unRegister(String path) throws Exception {
        path = PathUtil.ensureStartWithSlash(path);
        curatorFramework.delete().forPath(path);
    }
}
