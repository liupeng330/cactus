package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.drainage.bean.*;
import com.qunar.corp.cactus.drainage.service.DrainageInfoDao;
import com.qunar.corp.cactus.drainage.service.DrainageManager;
import com.qunar.corp.cactus.drainage.service.DrainageService;
import com.qunar.corp.cactus.drainage.service.StandardHttpService;
import com.qunar.corp.cactus.drainage.tools.GlobalConfig;
import com.qunar.corp.cactus.drainage.tools.IpHostHelper;
import com.qunar.corp.cactus.event.UserOperationEvent;
import com.qunar.corp.cactus.event.UserOperationLogger;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;
import com.qunar.corp.cactus.web.util.UrlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import qunar.api.pojo.node.JacksonSupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.qunar.corp.cactus.drainage.constant.Constants.BETA_URL;

/**
 * @author sen.chai
 * @date 2015-04-23 16:16
 */
@Service
public class DefaultDrainageManager implements DrainageManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDrainageManager.class);

    private static final Map<RunningStatus, String> errorMessageMap = ImmutableMap.of(
            RunningStatus.start, "开启引流, betaIp是",
            RunningStatus.stop, "关闭引流 "
    );
    public static final String DRAINAGE_SUCCESS = "引流成功 ";

    private Map<RunningStatus, ? extends Handler> handlerMap;

    @Resource
    private DrainageService drainageService;

    @Resource
    private ProviderService providerService;

    @Resource
    private UserOperationLogger userOperationLogger;

    @Resource
    private DrainageInfoDao drainageInfoDao;

    @Resource
    private StandardHttpService standardHttpService;


    @PostConstruct
    public void init() {
        handlerMap = ImmutableMap.of(
                RunningStatus.start, new Handler() {
                    @Override
                    public DrainageResultDesc handle(String service, String methodName, Set<DrainageGroup> targetGroups, Set<DrainageIpAndPort> providerSet) {
                        DrainageResultDesc resultDesc = new DrainageResultDesc();
                        for (DrainageIpAndPort providerUrl : providerSet) {
                            try {
                                startOneProvider(service, methodName, targetGroups, providerUrl);
                                resultDesc.appendSuccessMsg(providerUrl.buildHostFormalString()).appendSuccessMsg(DRAINAGE_SUCCESS);
                            } catch (Exception e) {
                                resultDesc.incError().appendFailedMsg(e.getMessage());
                            }
                        }
                        return resultDesc;
                    }
                },
                RunningStatus.stop, new Handler() {
                    @Override
                    public DrainageResultDesc handle(String service, String methodName, Set<DrainageGroup> targetGroups, Set<DrainageIpAndPort> providerSet) {
                        DrainageResultDesc resultDesc = new DrainageResultDesc();
                        try {
                            DrainageParam param = buildDrainageParam(Collections.EMPTY_SET, service, methodName, providerSet);
                            drainageService.stop(param);
                            resultDesc.appendSuccessMsg(providerSet.toString());
                        } catch (Exception e) {
                            resultDesc.incError().appendFailedMsg(e.getMessage());
                        }
                        return resultDesc;
                    }
                }
        );
    }


    @Override
    public void stopDrainage(String service, String methodName, long zkId, String serviceKey, String group) {
        List<DrainageIpAndPort> serviceIpAndPorts = drainageInfoDao.queryServiceDrainageInfo(service, methodName);
        handleDrainageOp(RunningStatus.stop, service, methodName, zkId, serviceKey, group, Collections.EMPTY_SET, Sets.newHashSet(serviceIpAndPorts));
    }
    //开始引流
    @Override
    public void startDrainage(String service, String methodName, long zkId, String serviceKey, String group,
    		  Set<DrainageGroup> targetGroups, Set<DrainageIpAndPort> providerSet) {
        handleDrainageOp(RunningStatus.start, service, methodName, zkId, serviceKey, group, targetGroups, providerSet);
    }
    
    private void handleDrainageOp(RunningStatus status, String service, String methodName, long zkId, String serviceKey, String group, Set<DrainageGroup> targetGroups, Set<DrainageIpAndPort> providerSet) {
        logger.info("{} 引流: service={}, method={}, provider={}, targetGroups={}, serviceKey={}, group={}", status,
                service, methodName, providerSet, targetGroups, serviceKey, group);
        DrainageResultDesc resultDesc = handlerMap.get(status).handle(service, methodName, targetGroups, providerSet);
        recordOpLog(zkId, group, status, targetGroups, serviceKey, providerSet, resultDesc);
        if (resultDesc.hasError()) {
            throw new RuntimeException(resultDesc.buildAllDesc());
        }
    }


    private void recordOpLog(long zkId, String group, RunningStatus status, Set<DrainageGroup> targetGroups,
                             String serviceKey, Set<DrainageIpAndPort> providerSet, DrainageResultDesc resultDesc) {
        try {
            ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, "", serviceKey);
            String ipAndPortString = CollectionUtils.isEmpty(providerSet) ? DrainageIpAndPort.defaultFormalString() : providerSet.iterator().next().buildFormalString();
            ServiceSign tempServiceSign = ServiceSign.makeServiceSign(zkId, group, ipAndPortString,
                    serviceSign.getServiceGroup(), serviceSign.getServiceInterface(), serviceSign.getVersion());
            StringBuilder opLogContent = new StringBuilder()
                    .append(errorMessageMap.get(status))
                    .append(CollectionUtils.isEmpty(targetGroups) ? "" : targetGroups)
                    .append(resultDesc.getFailedMsg())
                    .append(resultDesc.getSuccessMsg());
            UserOperationEvent operationEvent = new UserOperationEvent(UserContainer.getUserId(), tempServiceSign,
                    opLogContent.toString());
            userOperationLogger.logOperation(operationEvent);
        } catch (Exception e) {
            logger.error("record opLog failed", e);
        }
    }


    private void startOneProvider(String service, String methodName, Set<DrainageGroup> targetGroups, DrainageIpAndPort providerUrl) {
        String providerIp = providerUrl.getIp();
        int providerPort = providerUrl.getPort();
        try {
            logger.info("begin drainage provider {}:{}", providerIp, providerPort);
            DrainageParam param = buildDrainageParam(targetGroups, service, methodName, Sets.newHashSet(providerUrl));         
            drainageService.start(param);
        } catch (Exception e) {
            logger.error("drainage error, provider:{}", providerIp, providerPort, e);
            throw new RuntimeException("引流provider:  " + providerUrl.buildHostFormalString() + "时出错, reason:" + e.getMessage());
        }
    }

    private List<URL> resolveEnabledProviders(long zkId, ServiceSign serviceSign) {
        List<URL> urls = providerService.getConfiguredProviders(serviceSign.getGroup(), serviceSign.getServiceInterface(),
                UrlHelper.zkIdEqual(zkId), ProviderPredicates.serviceKeyEqual(serviceSign), new Predicate<URL>() {
                    @Override
                    public boolean apply(URL provider) {
                        String disabled = provider.getParameter("disabled");
                        return Strings.isNullOrEmpty(disabled) && !Boolean.valueOf(disabled);
                    }
                }).transform(UrlConverter.providerUrlToShow()).toList();
        if (CollectionUtils.isEmpty(urls)) {
            logger.error("No providers found, ServiceSign={}", serviceSign);
            throw new RuntimeException("No providers found");
        }
        return FluentIterable.from(urls).filter(new Predicate<URL>() {
            @Override
            public boolean apply(URL input) {
                Map<String, String> parameters = input.getParameters();
                String disabled = parameters.get("disabled");
                logger.info("disabled: {}", disabled);
                return Strings.isNullOrEmpty(disabled) || !Boolean.parseBoolean(disabled);
            }
        }).toList();
    }


    @Override
    public boolean serviceIsDrainaging(String serviceName) {
        try {
            return drainageInfoDao.queryCountByServiceName(serviceName) > 0;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }


    private DrainageParam buildDrainageParam(Set<DrainageGroup> targetGroups, String serviceName,
                                             String methodName, Set<DrainageIpAndPort> serviceSet) {
        DrainageParam param = new DrainageParam();
        param.setServiceIpAndPort(serviceSet);
        param.setServiceName(serviceName);
        param.setMethodName(methodName);
        param.setTargetGroups(targetGroups);
        return param;
    }


    @Override
    public List<DrainageIpAndPort> getProviderAddress(String service, String methodName, long zkId,
                                                      String serviceKey, String group) {
        ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, "", serviceKey);
        List<URL> urls = resolveEnabledProviders(zkId, serviceSign);
        return Lists.newArrayList(Sets.newHashSet(Lists.transform(urls, new Function<URL, DrainageIpAndPort>() {
            @Override
            public DrainageIpAndPort apply(URL input) {
                return DrainageIpAndPort.of(IpHostHelper.toHostIfIp(input.getIp()), input.getPort());
            }
        })));
    }

    @Override
    public Object getBetaAddress(String service, String methodName, long zkId,
                                 String serviceKey, String group) {

        Map<String, String> param = Maps.newHashMap();
        param.put("service", service);
        param.put("methodName", methodName);
        param.put("zkId", String.valueOf(zkId));
        param.put("serviceKey", serviceKey);
        param.put("group", group);

        try {
            HttpInvokeResult httpInvokeResult = standardHttpService.invokeWithResult(GlobalConfig.get(BETA_URL), param, new Function<String, Object>() {
                @Override
                public Object apply(String input) {
                    return JacksonSupport.parseJson(input, HttpInvokeResult.class).getData();
                }
            });
            logger.info("getBetaAddress: {}", JacksonSupport.toJson(httpInvokeResult));
            return httpInvokeResult.getData();
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }

    }


    interface Handler {
        DrainageResultDesc handle(String service, String methodName, Set<DrainageGroup> targetGroups, Set<DrainageIpAndPort> providerSet);
    }

}

