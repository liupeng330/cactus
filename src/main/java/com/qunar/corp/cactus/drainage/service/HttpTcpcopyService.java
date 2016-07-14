
package com.qunar.corp.cactus.drainage.service;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.drainage.bean.TcpcopyParam;
import com.qunar.corp.cactus.drainage.tools.GlobalConfig;
import com.qunar.corp.cactus.util.CommonCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.api.pojo.node.JacksonSupport;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author sen.chai
 * @date 2015-04-29 19:10
 */
@Service
public class HttpTcpcopyService implements TcpcopyService {

    private static final Logger logger = LoggerFactory.getLogger(HttpTcpcopyService.class);
    public static final String SERVICEIP = "serviceip";
    public static final String INTERCEPTIP = "interceptip";
    public static final String SERVICEPORT = "serviceport";
    public static final String PROXYIP = "proxyip";
    public static final String PROXYPORT = "proxyport";
    public static final String ROUTEIP = "routeip";
    public static final String ACTION = "action";
    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String OPS_EXEC_CMD_URL = "ops.exec.cmd.url";

    public static final int SUCCESS_STATUS = 0;
    public static final int FAIL_STATUS_START_FAILED = 1;
    public static final int FAIL_STATUS_PROCESS_EXISTS = 2;

    public static final Map<Integer, String> startStatusMesssage = ImmutableMap.of(
            SUCCESS_STATUS, "执行成功",
            FAIL_STATUS_START_FAILED, "执行失败, 进程启动失败",
            FAIL_STATUS_PROCESS_EXISTS, "执行失败, 进程已经存在"
    );
    public static final int DEFAULT_RETRY_INTERVAL = 50;
    public static final int DEFAULT_RETRY_COUNT = 3;


    @Resource
    private StandardHttpService standardHttpService;

    //serviceip=l-syndicmaster1.ops.cn6&interceptip=192.168.237.222&serviceport=20881&proxyip=192.168.237.141&proxyport=30880&routeip=192.168.236.225&action=start

    @Override
    public void start(TcpcopyParam tcpcopyParam) {
        final String url = GlobalConfig.get(OPS_EXEC_CMD_URL);
        Map<String, String> startParam = buildParam(tcpcopyParam, ACTION_START);
        doInvokeWithRetry(url, startParam);
    }

    private void doInvokeWithRetry(final String url, final Map<String, String> startParam) {
        int retryCount = getRetryCount();
        long retryInterval = getRetryInterval();
        RetryInvoker.of(retryCount, retryInterval).invoke(new RetryInvoker.VoidInvokeHolder() {
            @Override
            public void invoke() {
                doInvoke(url, startParam);
            }
        });
    }

    private int getRetryCount() {
        try {
            return GlobalConfig.getInt("ops.exec.retry.count");
        } catch (Exception e) {
            return DEFAULT_RETRY_COUNT;
        }
    }

    private void doInvoke(String url, final Map<String, String> httpParam) {
        try {
            logger.info("begin invoke ops: {},{}", url, httpParam);
            standardHttpService.invokeIgnoreResult(url, httpParam, new Function<String, Object>() {
                @Override
                public Object apply(String responseBody) {
                    Map map = JacksonSupport.parseJson(responseBody, Map.class);
                    String serviceHost = httpParam.get(SERVICEIP);
                    List<Map> list = (List<Map>) map.get("data");
                    for (Map resultItem : list) {
                        Object hostResultInfo = resultItem.get(serviceHost);
                        try {
                            int status = Integer.parseInt(String.valueOf(hostResultInfo));
                            logger.info("invoke ops result: {}", startStatusMesssage.get(status));
                            if (status != SUCCESS_STATUS && status != FAIL_STATUS_PROCESS_EXISTS) {
                                throw new RuntimeException(startStatusMesssage.get(status));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(String.format("%s,ops调用失败, reason: %s, %s",
                                    serviceHost, hostResultInfo, e.getMessage()), e);
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("invoke ops failed, {}", httpParam, e);
            throw Throwables.propagate(e);
        }
    }


    private Map<String, String> buildParam(TcpcopyParam tcpcopyParam, String action) {
        Map<String, String> param = Maps.newHashMapWithExpectedSize(8);
        param.put(SERVICEIP, CommonCache.getHostNameByIp(tcpcopyParam.getServiceIp()));
        param.put(INTERCEPTIP, tcpcopyParam.getInterceptIp());
        param.put(SERVICEPORT, String.valueOf(tcpcopyParam.getServicePort()));
        param.put(PROXYIP, tcpcopyParam.getProxyIp());
        param.put(PROXYPORT, String.valueOf(tcpcopyParam.getProxyPort()));
        param.put(ROUTEIP, tcpcopyParam.getRouteIp());
        param.put(ACTION, action);
        return param;
    }

    @Override
    public void stop(TcpcopyParam tcpcopyParam) {
        final String url = GlobalConfig.get(OPS_EXEC_CMD_URL);
        Map<String, String> startParam = buildParam(tcpcopyParam, ACTION_STOP);
        doInvokeWithRetry(url, startParam);
    }

    public long getRetryInterval() {
        try {
            return GlobalConfig.getLong("ops.exec.retry.interval");
        } catch (Exception e) {
            return DEFAULT_RETRY_INTERVAL;
        }
    }
}
