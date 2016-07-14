package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.event.UserOperationEvent;
import com.qunar.corp.cactus.service.governance.IGovernanceService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.governance.router.RouterConstants;
import com.qunar.corp.cactus.service.governance.router.RouterHelper;
import com.qunar.corp.cactus.service.governance.router.RouterPreviewService;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.support.protocol.GovernanceFactory;
import com.qunar.corp.cactus.util.*;
import com.qunar.corp.cactus.web.model.*;
import com.qunar.corp.cactus.web.util.UrlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;
import java.util.*;

import static com.qunar.corp.cactus.util.ConstantHelper.EMPTY_ID;
import static com.qunar.corp.cactus.util.ExceptionInfoHelper.buildVariousExceptionInfo;

/**
 * Date: 13-10-29 Time: 下午2:53
 *
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/router")
public class RouterController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(RouterController.class);

    @Resource
    private EventBus eventBus;

    @Resource
    private IQueryGovernanceService queryGovernanceService;

    @Resource(name = "routerGovernanceService")
    private IGovernanceService governanceService;

    @Resource
    private RouterPreviewService routerPreviewService;

    @Resource
    private ProviderService providerService;

    @Resource
    private ConsumerService consumerService;

    @Resource(name="routerFactory")
    private GovernanceFactory routerFactory;

    @RequestMapping("/showRouter")
    public String showRouter(@RequestParam("group") String group,
            @RequestParam("serviceKey") String serviceKey, @RequestParam("zkId") long zkId, Model model) {
        try {
            model.addAttribute("group", group);
            model.addAttribute("serviceKey", serviceKey);
            model.addAttribute(ConstantHelper.ZKID, zkId);
            model.addAttribute(
                    "userDataList",
                    queryGovernanceService
                            .getServiceKeyMatchedGovernanceDatas(
                                    GovernanceQuery.createBuilder()
                                            .fromServiceSign(ServiceSign.makeServiceSign(zkId, group, serviceKey))
                                            .pathType(PathType.ROUTER).dataType(DataType.USERDATA)
                                            .notStatus(Status.DEL).build()).transform(TO_GOVERNANCE_VIEW_FUNC)
                            .toSortedList(COMPARE_STATUS_AND_LAST_UPDATE_TIME));
            return "router/showRouter";
        } catch (Exception e) {
            logger.error("An exception caught when query router info!", e);
        }
        return "redirect:/500.jsp";
    }

    @RequestMapping("/showDetail")
    public String showDetail(@RequestParam("id") long id, Model model) {
        GovernanceData governanceData = null;
        try {
            governanceData = queryGovernanceService.getGovernanceDataById(id);
            model.addAttribute("router", TO_GOVERNANCE_VIEW_FUNC.apply(governanceData));

            Map<String, String> routeParams = RouterHelper.getRouteParams(governanceData.getUrl());
            model.addAttribute("routerParams", routeParams);

            model.addAttribute(RouterConstants.CONSUMER_MATCH_HOST,
                    sort(toIpAndHosts(routeParams.get(RouterConstants.CONSUMER_MATCH_HOST))));
            model.addAttribute(RouterConstants.CONSUMER_MISMATCH_HOST,
                    sort(toIpAndHosts(routeParams.get(RouterConstants.CONSUMER_MISMATCH_HOST))));
            model.addAttribute(RouterConstants.PROVIDER_MATCH_HOST,
                    sort(toIpAndHosts(routeParams.get(RouterConstants.PROVIDER_MATCH_HOST))));
            model.addAttribute(RouterConstants.PROVIDER_MISMATCH_HOST,
                    sort(toIpAndHosts(routeParams.get(RouterConstants.PROVIDER_MISMATCH_HOST))));
            return "router/showDetail";
        } catch (Exception e) {
            logger.error("show router detail error, id=[{}], {}", id, governanceData, e);
        }
        return "redirect:/500.jsp";
    }

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();

    private List<Pair<String, String>> toIpAndHosts(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return ImmutableList.of();
        }

        List<String> ips = COMMA_SPLITTER.splitToList(input);
        List<Pair<String, String>> ipAndHosts = Lists.newArrayListWithCapacity(ips.size());
        for (String ip : ips) {
            if (IpAndPort.isLegalIp(ip)) {
                ipAndHosts.add(Pair.makePair(ip, CommonCache.getHostNameByIp(ip)));
            } else {
                ipAndHosts.add(Pair.makePair(ip, ip));
            }
        }
        return ipAndHosts;
    }

    private List<Pair<String, String>> sort(List<Pair<String, String>> ipAndHosts) {
        List<Pair<String, String>> result = Lists.newArrayListWithCapacity(ipAndHosts.size());
        List<Pair<String, String>> unResolveIps = Lists.newArrayList();

        Map<Long, Pair<String, String>> ipMapping = Maps.newTreeMap();
        for (Pair<String, String> ipAndHost : ipAndHosts) {
            String ip = ipAndHost.getLeft();
            if (IpAndPort.isLegalIp(ip)) {
                ipMapping.put(IpAndPort.ipToLong(ip), ipAndHost);
            } else {
                unResolveIps.add(ipAndHost);
            }
        }

        result.addAll(ipMapping.values());

        result.addAll(Ordering.from(new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> lhs, Pair<String, String> rhs) {
                return lhs.getLeft().compareTo(rhs.getLeft());
            }
        }).immutableSortedCopy(unResolveIps));

        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/addPageRender")
    public String addPageRender(@RequestParam(value = "id", required = false, defaultValue = EMPTY_ID + "") long id,
            @RequestParam("group") String group,
            @RequestParam("serviceKey") String serviceKey, @RequestParam("zkId") long zkId, Model model) {
        ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, serviceKey);
        model.addAttribute("serviceSign", serviceSign);
        model.addAttribute(
                "methodList",
                providerService.getMethods(serviceSign.getGroup(), serviceSign.serviceInterface,
                        ProviderPredicates.serviceKeyEqual(serviceSign), UrlHelper.zkIdEqual(serviceSign.zkId)));

        FluentIterable<URL> providers = providerService.getItems(serviceSign.getGroup(), serviceSign.getServiceInterface(),
                ProviderPredicates.serviceKeyEqual(serviceSign), UrlHelper.zkIdEqual(serviceSign.zkId));
        List<Pair<String, String>> providerIpAndHosts = providers.transform(
                new Function<URL, Pair<String, String>>() {
                    @Override
                    public Pair<String, String> apply(URL input) {
                        return new Pair<String, String>(input.getIp(), CommonCache.getHostNameByIp(input.getIp()));
                    }
                }).toSet().asList();

        List<String> providerPorts = providers.transform(new Function<URL, String>() {
            @Override
            public String apply(URL input) {
                return String.valueOf(input.getPort());
            }
        }).toSet().asList();

        model.addAttribute("ipList", sort(providerIpAndHosts));
        model.addAttribute("portList", providerPorts);

        FluentIterable<URL> consumers = consumerService.getItems(serviceSign.getGroup(), serviceSign.serviceInterface,
                UrlHelper.matchServiceKey(serviceSign), UrlHelper.zkIdEqual(serviceSign.zkId));
        List<Pair<String, String>> consumerIpAndHosts = consumers.transform(new Function<URL, Pair<String, String>>() {
            @Override
            public Pair<String, String> apply(URL input) {
                return new Pair<String, String>(input.getIp(), CommonCache.getHostNameByIp(input.getIp()));
            }
        }).toSet().asList();
        List<String> consumerApps = consumers.transform(new Function<URL, String>() {
            @Override
            public String apply(URL input) {
                return input.getParameter(Constants.APPLICATION_KEY);
            }
        }).filter(Predicates.notNull()).toSet().asList();
        model.addAttribute("consumerList", sort(consumerIpAndHosts));
        model.addAttribute("applicationList", consumerApps);

        if (id != EMPTY_ID) {
            GovernanceData governanceData = queryGovernanceService.getGovernanceDataById(id);
            if (governanceData != null) {
                Map<String, String> param = RouterHelper.getRouteParams(governanceData.getUrl());
                param.put(ConstantHelper.NAME, governanceData.getName());
                model.addAttribute("params", param);
            }
        }
        model.addAttribute("id", id);
        model.addAttribute(ConstantHelper.ZKID, zkId);
        return "router/add";
    }

    @RequestMapping("/add")
    @JsonBody
    public Object add(@RequestParam(value = "id", required = false, defaultValue = EMPTY_ID + "") long id,
            @RequestParam("group") String group,
            @RequestParam("name") String name, @RequestParam("priority") String priority,
            @RequestParam("serviceKey") String serviceKey, @RequestParam("zkId") long zkId,
            @RequestParam("matchMethods") String matchMethods, @RequestParam("misMatchMethods") String misMatchMethods,
            @RequestParam("consumerMatchIp") String consumerMatchIp,
            @RequestParam("consumerMismatchIp") String consumerMismatchIp, @RequestParam("matchApp") String matchApp,
            @RequestParam("mismatchApp") String mismatchApp, @RequestParam("providerMatchIp") String providerMatchIp,
            @RequestParam("providerMismatchIp") String providerMismatchIp,
            @RequestParam("providerMatchPort") String providerMatchPort,
            @RequestParam("providerMismatchPort") String providerMismatchPort) {
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put(ConstantHelper.NAME, name);
        paramMap.put(Constants.PRIORITY_KEY, priority);
        paramMap.put(RouterConstants.MATCH_METHOD, matchMethods);
        paramMap.put(RouterConstants.MISMATCH_METHOD, misMatchMethods);
        paramMap.put(RouterConstants.CONSUMER_MATCH_HOST, consumerMatchIp);
        paramMap.put(RouterConstants.CONSUMER_MISMATCH_HOST, consumerMismatchIp);
        paramMap.put(RouterConstants.CONSUMER_MATCH_APP, matchApp);
        paramMap.put(RouterConstants.CONSUMER_MISMATCH_APP, mismatchApp);
        paramMap.put(RouterConstants.PROVIDER_MATCH_HOST, providerMatchIp);
        paramMap.put(RouterConstants.PROVIDER_MISMATCH_HOST, providerMismatchIp);
        paramMap.put(RouterConstants.PROVIDER_MATCH_PORT, providerMatchPort);
        paramMap.put(RouterConstants.PROVIDER_MISMATCH_PORT, providerMismatchPort);

        ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, Constants.ANYHOST_VALUE, serviceKey);
        logger.info("add router, {}, param={}", serviceSign, paramMap);

        if (id == EMPTY_ID) {
            try {
                governanceService.addDatas(Arrays.asList(serviceSign), paramMap);
            } catch (Exception e) {
                logger.error("occur error when add router, {}, param={}", serviceSign, paramMap, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        } else {
            GovernanceData governanceData = null;
            try {
                governanceData = queryGovernanceService.getGovernanceDataById(id);

                String result = getCheckDataResult(governanceData);
                if (!result.equals(VALID_DATA)) {
                    logger.warn("can not find router, id={}", id);
                    return errorJson(result);
                }

                if (governanceData.getName() != null && !governanceData.getName().equals(name)) {
                    logger.warn("can not change router name");
                    return errorJson("配置名称不能修改！");
                }

                governanceService.changeDatas(governanceData, paramMap);
            } catch (Exception e) {
                logger.error("occur error when add router, {}, param={}", governanceData, paramMap, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        }
        return true;
    }

    @RequestMapping("/delete")
    @JsonBody
    public Object delete(@RequestParam("group") String group, @RequestBody GovernanceData governanceData) {
        try {
            logger.info("delete router, {}", governanceData);
            governanceService.delete(governanceData);
        } catch (Exception e) {
            logger.error("occur error when delete router, {}", governanceData, e);
            return errorJson(buildVariousExceptionInfo(e));
        }
        return true;
    }

    private static final int ENABLE_STATE = Status.ENABLE.code;
    private static final int DISABLE_STATE = Status.DISABLE.code;

    @RequestMapping("/changeEnabledState/{status}")
    @JsonBody
    public Object enabled(@RequestParam("group") String group, @RequestBody GovernanceData governanceData,
                          @PathVariable int status) {
        String operateENStr = status == ENABLE_STATE ? "enable a router" : "disable a router";
        logger.info("{}, {}", operateENStr, governanceData);
        String zkName = getZKName(governanceData.getZkId());
        if (Strings.isNullOrEmpty(zkName)) {
            return errorJson("无效的zk id");
        }

        if (status != ENABLE_STATE && status != DISABLE_STATE) {
            logger.warn("invalid state type [{}]", status);
            return errorJson("无效的操作类型");
        }

        if (governanceData.getStatus().code == Status.DEL.code) {
            logger.warn("can not operate deleted router");
            return errorJson("不能对删除状态的配置进行操作！");
        }

        try {
            if (status == ENABLE_STATE) {
                governanceService.enable(governanceData);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        governanceData.getServiceSign(),
                        zkName + "启用路由: " + URL.decode(governanceData.getUrlStr())));
            } else {
                governanceService.disable(governanceData);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        governanceData.getServiceSign(),
                        zkName + "禁用路由: " + URL.decode(governanceData.getUrlStr())));
            }
        } catch (Exception e) {
            logger.error("occur error when {} router, {}", operateENStr, governanceData, e);
            return errorJson(buildVariousExceptionInfo(e));
        }
        return true;
    }

    @RequestMapping("/previewWithParam")
    public String previewWithParam(@RequestParam("zkId") long zkId, @RequestParam("group") String group,
            @RequestParam("name") String name,
            @RequestParam("priority") String priority, @RequestParam("serviceKey") String serviceKey,
            @RequestParam("matchMethods") String matchMethods, @RequestParam("misMatchMethods") String misMatchMethods,
            @RequestParam("consumerMatchIp") String consumerMatchIp,
            @RequestParam("consumerMismatchIp") String consumerMismatchIp, @RequestParam("matchApp") String matchApp,
            @RequestParam("mismatchApp") String mismatchApp, @RequestParam("providerMatchIp") String providerMatchIp,
            @RequestParam("providerMismatchIp") String providerMismatchIp,
            @RequestParam("providerMatchPort") String providerMatchPort,
            @RequestParam("providerMismatchPort") String providerMismatchPort, Model model) {
        Map<String, String> paramMap = Maps.newHashMap();
        checkNotEmptyAndPut(paramMap, ConstantHelper.NAME, name);
        checkNotEmptyAndPut(paramMap, Constants.PRIORITY_KEY, priority);
        checkNotEmptyAndPut(paramMap, RouterConstants.MATCH_METHOD, matchMethods);
        checkNotEmptyAndPut(paramMap, RouterConstants.MISMATCH_METHOD, misMatchMethods);
        checkNotEmptyAndPut(paramMap, RouterConstants.CONSUMER_MATCH_HOST, consumerMatchIp);
        checkNotEmptyAndPut(paramMap, RouterConstants.CONSUMER_MISMATCH_HOST, consumerMismatchIp);
        checkNotEmptyAndPut(paramMap, RouterConstants.CONSUMER_MATCH_APP, matchApp);
        checkNotEmptyAndPut(paramMap, RouterConstants.CONSUMER_MISMATCH_APP, mismatchApp);
        checkNotEmptyAndPut(paramMap, RouterConstants.PROVIDER_MATCH_HOST, providerMatchIp);
        checkNotEmptyAndPut(paramMap, RouterConstants.PROVIDER_MISMATCH_HOST, providerMismatchIp);
        checkNotEmptyAndPut(paramMap, RouterConstants.PROVIDER_MATCH_PORT, providerMatchPort);
        checkNotEmptyAndPut(paramMap, RouterConstants.PROVIDER_MISMATCH_PORT, providerMismatchPort);
        ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, Constants.ANYHOST_VALUE, serviceKey);
        setModelAttr(model, serviceSign, paramMap);
        return "router/preview";
    }

    private void checkNotEmptyAndPut(Map<String, String> paramMap, String key, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            paramMap.put(key, value);
        }
    }

    @RequestMapping("/previewWithUrl")
    public String previewWithUrl(@RequestParam("group") String group,
            @RequestParam("serviceKey") String serviceKey, @RequestParam("encodeUrl") String encodeUrl,
            @RequestParam("zkId") long zkId, Model model) {
        Map<String, String> paramMap = RouterHelper.getRouteParams(URL.valueOf(encodeUrl));
        // HttpServletRequest总是返回解码后的servlet参数,所以这里的encodeUrl是解码后的url，不需要再解码
        ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, Constants.ANYHOST_VALUE, serviceKey);
        setModelAttr(model, serviceSign, paramMap);
        return "router/preview";
    }

    @RequestMapping("/preview")
    public String preview(@RequestParam("group") String group,
            @RequestParam("serviceKey") String serviceKey, @RequestParam("zkId") long zkId, Model model) {
        ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, Constants.ANYHOST_VALUE, serviceKey);
        setModelAttr(model, serviceSign, null);
        return "router/preview";
    }

    private void setModelAttr(Model model, ServiceSign serviceSign, Map<String, String> paramMap) {
        try {
            Map<URL, Map<Invocation, List<Invoker<Object>>>> invokeRelationMap = null;
            if (paramMap == null) {
                invokeRelationMap = routerPreviewService.preview(serviceSign);
            } else {
                invokeRelationMap = routerPreviewService.preview(serviceSign, paramMap);
                model.addAttribute("newRouter", new GovernanceView(routerFactory.create(serviceSign, paramMap)));
            }

            model.addAttribute("invokeRelationList", toInvokeRelationList(invokeRelationMap));
            model.addAttribute(
                    "routerList",
                    queryGovernanceService
                            .getServiceKeyMatchedGovernanceDatas(
                                    GovernanceQuery.createBuilder().fromServiceSign(serviceSign)
                                            .pathType(PathType.ROUTER).dataType(DataType.USERDATA)
                                            .status(Status.ENABLE).build()).transform(TO_GOVERNANCE_VIEW_FUNC).toList());
        } catch (Exception e) {
            logger.error("occur error set preview router, {}, paramMap={}", serviceSign, paramMap, e);
            model.addAttribute("errorInfo", buildVariousExceptionInfo(e));
        }
    }

    private List<InvokeRelationship> toInvokeRelationList(
            Map<URL, Map<Invocation, List<Invoker<Object>>>> invokeRelationMap) {
        Map<Set<Provider>, Map<URL, Set<String>>> result = toInvokeMap(invokeRelationMap);
        return translate(result);
    }

    private List<InvokeRelationship> translate(Map<Set<Provider>, Map<URL, Set<String>>> origin) {
        List<InvokeRelationship> result = Lists.newArrayListWithCapacity(origin.size());
        for (Map.Entry<Set<Provider>, Map<URL, Set<String>>> entry : origin.entrySet()) {
            result.add(new InvokeRelationship(entry.getKey(), getConsumers(entry.getValue())));
        }
        return result;
    }

    private List<ConsumerSign> getConsumers(Map<URL, Set<String>> value) {
        List<ConsumerSign> result = Lists.newArrayListWithCapacity(value.size());
        for (Map.Entry<URL, Set<String>> entry : value.entrySet()) {
            result.add(new ConsumerSign(consumerCache.getUnchecked(entry.getKey()), entry.getValue()));
        }
        return result;
    }

    private Map<Set<Provider>, Map<URL, Set<String>>> toInvokeMap(
            Map<URL, Map<Invocation, List<Invoker<Object>>>> invokeRelationMap) {
        Map<Set<Provider>, Map<URL, Set<String>>> result = Maps.newHashMap();
        for (Map.Entry<URL, Map<Invocation, List<Invoker<Object>>>> consumerMethodProvider : invokeRelationMap
                .entrySet()) {
            URL consumerURL = consumerMethodProvider.getKey();
            for (Map.Entry<Invocation, List<Invoker<Object>>> methodProviders : consumerMethodProvider.getValue()
                    .entrySet()) {
                Set<Provider> providers = FluentIterable.from(methodProviders.getValue())
                        .transform(INVOKER_TO_PROVIDER_FUNC).toSet();
                Map<URL, Set<String>> resultConsumerInfo = result.get(providers);
                if (resultConsumerInfo == null) {
                    resultConsumerInfo = Maps.newHashMap();
                    result.put(providers, resultConsumerInfo);
                }
                Set<String> resultMethodInfo = resultConsumerInfo.get(consumerURL);
                if (resultMethodInfo == null) {
                    resultMethodInfo = Sets.newHashSet();
                    resultConsumerInfo.put(consumerURL, resultMethodInfo);
                }
                resultMethodInfo.add(methodProviders.getKey().getMethodName());
            }
        }
        return result;
    }

    private static final Function<Invoker<Object>, Provider> INVOKER_TO_PROVIDER_FUNC = new Function<Invoker<Object>, Provider>() {
        @Override
        public Provider apply(Invoker<Object> input) {
            return providerCache.getUnchecked(input);
        }
    };

    private static final LoadingCache<Invoker<Object>, Provider> providerCache = CacheBuilder.newBuilder()
            .maximumSize(200).build(new CacheLoader<Invoker<Object>, Provider>() {
                @Override
                public Provider load(Invoker<Object> invoker) throws Exception {
                    return UrlConverter.getProviderFromUrl(invoker.getUrl(), null);// 注意：这里转化出来的provider对象的group值为null
                }
            });

    private static final LoadingCache<URL, Consumer> consumerCache = CacheBuilder.newBuilder().maximumSize(200)
            .build(new CacheLoader<URL, Consumer>() {
                @Override
                public Consumer load(URL url) throws Exception {
                    return UrlConverter.getConsumerFromUrl(url, null);// 注意：这里转化出来的consumer对象的group值为null
                }
            });
}