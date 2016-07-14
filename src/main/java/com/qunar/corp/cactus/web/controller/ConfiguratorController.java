package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.event.UserOperationEvent;
import com.qunar.corp.cactus.service.governance.IGovernanceService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.governance.config.*;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.*;
import com.qunar.corp.cactus.web.model.Provider;
import com.qunar.corp.cactus.web.util.UrlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;
import java.util.*;

import static com.qunar.corp.cactus.util.ConstantHelper.*;
import static com.qunar.corp.cactus.util.ExceptionInfoHelper.buildVariousExceptionInfo;

/**
 * Date: 13-10-29 Time: 下午2:53
 *
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/configurator")
public class ConfiguratorController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(ConfiguratorController.class);

    @Resource
    private EventBus eventBus;

    @Resource
    private ProviderService providerService;

    @Resource
    private IQueryGovernanceService queryGovernanceService;

    @Resource(name = "configGovernanceService")
    private IGovernanceService configGovernanceService;

    @Resource
    private MockConfigService mockConfigService;

    @Resource
    private WeightConfigService weightConfigService;

    @Resource
    private LoadBalanceConfigService loadBalanceConfigService;

    @Resource
    private ProviderOnlineService onlineService;

    @Resource
    private NormalConfigService normalConfigService;

    private static final String ADDRESS_SEPARATOR = "#$";

    private static final Splitter ADDRESS_SPLITTER = Splitter.on(ADDRESS_SEPARATOR).trimResults().omitEmptyStrings();

    private static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();

    @Resource
    private LoadBalanceConfigService loadBalanceService;

    @RequestMapping("/showDetail")
    public String showDetail(@RequestParam("id") long id, Model model) {
        model.addAttribute("overrideUrl",
                TO_GOVERNANCE_VIEW_FUNC.apply(queryGovernanceService.getGovernanceDataById(id)));
        return "configurator/showDetail";
    }

    @RequestMapping("/showConfigurator")
    public String showConfiguratorList(@RequestParam("group") String group,
                                       @RequestParam("serviceGroup") String serviceGroup, @RequestParam("version") String version,
                                       @RequestParam("service") String service, @RequestParam("address") String address,
                                       @RequestParam("zkId") long zkId, Model model) {
        try {
            model.addAttribute("group", group);
            model.addAttribute("serviceGroup", serviceGroup);
            model.addAttribute("version", version);
            model.addAttribute("service", service);
            model.addAttribute("address", address);
            model.addAttribute(ConstantHelper.ZKID, zkId);
            model.addAttribute(
                    "userDataList",
                    queryGovernanceService
                            .getServiceKeyMatchedGovernanceDatas(
                                    GovernanceQuery.createBuilder().zkId(zkId).group(group).serviceName(service)
                                            .serviceGroup(serviceGroup).version(version).pathType(PathType.CONFIG)
                                            .dataType(DataType.USERDATA).notStatus(Status.DEL).build())
                            .transform(TO_GOVERNANCE_VIEW_FUNC).toSortedList(COMPARE_STATUS_AND_LAST_UPDATE_TIME));
            return "configurator/showConfigurator";
        } catch (Exception e) {
            logger.error("在查询configuration列表时出现异常!", e);
        }
        return "redirect:/500.jsp";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/addPageRender")
    public String addPageRender(@RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "id", required = false, defaultValue = EMPTY_ID + "") long id,
            @RequestParam("group") String group,
            @RequestParam("serviceGroup") String serviceGroup, @RequestParam("version") String version,
            @RequestParam("service") String serviceInterface, @RequestParam("address") String address,
            @RequestParam("zkId") long zkId,
            @RequestParam(value = "params", required = false, defaultValue = "") String params, Model model) {
        StringBuilder hostNameOfAddress = new StringBuilder();
        List<Provider> providerList = Lists.newArrayList();
        if (!address.equals(Constants.ANYHOST_VALUE)) {
            List<String> addressList = COMMA_SPLITTER.splitToList(address);
            for (String single : addressList) {
                hostNameOfAddress.append(",").append(CommonCache.address2HostNameAndPort(single));
            }
            if (hostNameOfAddress.length() > 0) {
                hostNameOfAddress.deleteCharAt(0);
            }
        } else {
            Provider defaultProvider = new Provider();
            defaultProvider.setAddress(address);
            defaultProvider.setHostNameAndPort(address);
            providerList.add(defaultProvider);
            hostNameOfAddress.append(Constants.ANYHOST_VALUE);
        }
        ServiceSign sign = ServiceSign.makeServiceSign(zkId, group, null, serviceGroup, serviceInterface, version);

        Predicate<URL> predicate = new ProviderPredicates.ServiceKeyPredicate(sign);
        providerList.addAll(providerService
                .getItems(sign.getGroup(), sign.getServiceInterface(), predicate, UrlHelper.zkIdEqual(zkId))
                .transform(new UrlConverter.URLToProviderFunction(group)).toSortedSet(new Comparator<Provider>() {
                    @Override
                    public int compare(Provider o1, Provider o2) {
                        return o1.getHostNameAndPort().compareTo(o2.getHostNameAndPort());
                    }
                }));

        model.addAttribute("name", name);
        model.addAttribute("id", id);
        model.addAttribute("group", group);
        model.addAttribute("serviceGroup", serviceGroup);
        model.addAttribute("version", version);
        model.addAttribute("service", serviceInterface);
        model.addAttribute("address", address);
        model.addAttribute("defaultHostName", hostNameOfAddress.toString());
        model.addAttribute("providerList", providerList);
        model.addAttribute("params", params);
        model.addAttribute(ConstantHelper.ZKID, zkId);
        return "configurator/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @JsonBody
    public Object add(@RequestParam(value = "id", required = false, defaultValue = EMPTY_ID + "") long id,
            @RequestParam("name") String name, @RequestParam("group") String group,
            @RequestParam(value = "serviceGroup") String serviceGroup,
            @RequestParam(value = "version") String version, @RequestParam("service") String service,
            @RequestParam("addresses") String addresses, @RequestParam("configParams") String params, @RequestParam("zkId") long zkId) {

        logger.info("add configurator, zkId={}, group={}, serviceGroup={}, interface={}, version={}, addresses={}, params={}",
                zkId, group, serviceGroup, service, version, addresses, params);

        List<String> addressList = COMMA_SPLITTER.splitToList(addresses);
        if (addressList.isEmpty()) {
            return errorJson("没有填入配置地址");
        }

        if (id == EMPTY_ID) {
            List<ServiceSign> serviceSigns = Lists.newArrayListWithCapacity(addressList.size());
            try {
                for (String address : addressList) {
                    if (!IpAndPort.ADDRESS_PATTERN.matcher(address).find()) {
                        logger.warn("illegal address: {}", address);
                        return errorJson(address + " 没有找到对应的ip，请重新点击左侧导航栏开始操作，如果错误依然出现，请联系管理员");
                    }
                    ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, address, serviceGroup, service, version);
                    serviceSigns.add(serviceSign);
                }

                normalConfigService.addNormalConfig(serviceSigns, getParamsMap(name, params));
            } catch (Exception e) {
                logger.error("add configurator error, {}, params={}", serviceSigns, params, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        } else {
            GovernanceData governanceData = queryGovernanceService.getGovernanceDataById(id);
            String result = getCheckDataResult(governanceData);

            if (!result.equals(VALID_DATA)) {
                return (result);

            }
            if (!governanceData.getName().trim().equals(name.trim())) {
                return errorJson("配置名称不能修改!");
            }

            if (!governanceData.getUrl().getAddress().trim().equals(addresses.trim())) {
                return errorJson("地址栏不能修改！");
            }

            try {
                normalConfigService.changeNormalConfig(governanceData, getParamsMap(name, params));
            } catch (Exception e) {
                logger.error("change configurator error, {}, params={}", governanceData, params, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        }
        return true;
    }

    private Map<String, String> getParamsMap(String name, String params) {
        Map<String, String> paramMap = StringUtils.parseQueryString(params);
        paramMap.put(ConstantHelper.NAME, name);
        return paramMap;
    }

    @RequestMapping("/updateMockRender")
    public String updateMockRender(@RequestParam("id") long id, Model model) {
        GovernanceData governanceData = queryGovernanceService.getGovernanceDataById(id);
        if (governanceData == null) {
            return "redirect:/400.jsp";
        }

        model.addAttribute("governanceView", TO_GOVERNANCE_VIEW_FUNC.apply(governanceData));
        model.addAttribute("mockList", MockEnum.values());
        return "configurator/mockEditor";
    }

    private static final int ONLINE_OPERATE = Status.ENABLE.code;
    private static final int OFFLINE_OPERATE = Status.DISABLE.code;

    @RequestMapping(value = "/changeEnabledState/{status}")
    @JsonBody
    public Object changeEnabledState(@RequestParam("group") String group, @RequestBody GovernanceData governanceData, @PathVariable int status) {
        String zkName = getZKName(governanceData.getZkId());
        if (Strings.isNullOrEmpty(zkName)) {
            return errorJson("无效的zk id");
        }

        if (status != Status.ENABLE.code && status != Status.DISABLE.code) {
            logger.warn("invalid operation [{}]", status);
            return errorJson("无效的操作类型");
        }

        if (governanceData.getStatus().code == Status.DEL.code) {
            logger.warn("is already disabled, {}", governanceData);
            return errorJson("不能对删除状态的配置进行操作！");
        }

        Status statusType = Status.fromCode(status);
        logger.info("{} {}", statusType.getText(), governanceData);
        try {
            if (statusType == Status.ENABLE) {
                configGovernanceService.enable(governanceData);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        governanceData.getServiceSign(),
                        zkName + "启用配置: " + governanceData.getUrlStr()));
            } else if (statusType == Status.DISABLE) {
                configGovernanceService.disable(governanceData);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        governanceData.getServiceSign(),
                        zkName + "禁用配置: " + governanceData.getUrlStr()));
            }
            return true;
        } catch (Exception e) {
            logger.error("occur error when {} {}", statusType.getText(), governanceData, e);
            return errorJson(buildVariousExceptionInfo(e));
        }
    }

    @RequestMapping(value = "/providerOnlineOrOffline", method = RequestMethod.POST)
    @JsonBody
    public Object providerOnlineOrOffline(@RequestParam("group") String group,
            @RequestParam("url") String url, @RequestParam("operationType") int operationType) {
        URL urlWithRegistryKey = URL.valueOf(url).addParameter(ConstantHelper.Q_REGISTRY_KEY, group)
                .addParameter(ConstantHelper.CACTUS_USER_NAME, UserContainer.getUserName())
                .addParameter(ConstantHelper.CACTUS_USER_ID, UserContainer.getUserId());
        ServiceSign serviceSign = ServiceSign.makeServiceSign(urlWithRegistryKey);

        if (operationType != ONLINE_OPERATE && operationType != OFFLINE_OPERATE) {
            logger.warn("invalid operation [{}]", operationType);
            return errorJson("无效的操作类型");
        }

        String operateENStr = operationType == ONLINE_OPERATE ? "online a provider" : "offline a provider";
        logger.info("{} {}", operateENStr, serviceSign);

        try {
            if (operationType == ONLINE_OPERATE) {
                onlineService.providerOnline(Arrays.asList(urlWithRegistryKey));
            } else {
                List<URL> lastProviders = onlineService.providerOffline(Arrays.asList(urlWithRegistryKey));
                if (!lastProviders.isEmpty()) {
                    logger.warn("last provider {}", lastProviders);
                    return errorJson("该provider是该服务下最后一台provider，无法下线！");
                }
            }
        } catch (Exception e) {
            logger.error("occur error when {} {}", operateENStr, serviceSign, e);
            return errorJson(buildVariousExceptionInfo(e));
        }
        return true;
    }

    // todo: 这里应该返回list
    @RequestMapping(value = "/batchOnlineOrOffline", method = RequestMethod.POST)
    @JsonBody
    public Object batchOnlineOrOffline(@RequestParam("group") String group,
                                       @RequestParam("addresses") String addresses,
                                       @RequestParam("operationType") int operationType) {
        String operateStr = operationType == ONLINE_OPERATE ? "batch online machines" : "batch offline machine";
        List<String> addressList = ADDRESS_SPLITTER.splitToList(addresses);
        List<URL> urls = Lists.newArrayList();
        try {
            if (operationType != ONLINE_OPERATE && operationType != OFFLINE_OPERATE) {
                logger.warn("invalid operation type [{}]", operationType);
                return errorJson("无效的操作类型");
            }

            urls = getUrls(group, addressList);
            logger.info("{} {}", operateStr, urls);
            if (operationType == ONLINE_OPERATE) {
                onlineService.providerOnline(urls);
            } else {
                List<URL> lastProviders = onlineService.providerOffline(urls);
                if (!lastProviders.isEmpty()) {
                    logger.warn("last provider {}", lastProviders);
                    return errorJson("该机器为下列服务的唯一provider，无法下线！" + LINE_SEPARATOR + lastProviders);
                }
            }
        } catch (Exception e) {
            logger.error("occur error when {} {}", operateStr, urls, e);
            return errorJson(buildVariousExceptionInfo(e));
        }
        return true;
    }

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private List<URL> getUrls(final String group, List<String> addresses) {
        List<URL> urls = Lists.newArrayList();
        for (final String address : addresses) {
            urls.addAll(providerService.getItems(group, new ProviderPredicates.AddressPredicate(address))
                    .transform(new Function<URL, URL>() {
                        @Override
                        public URL apply(URL input) {
                            return input.addParameter(CACTUS_USER_NAME, UserContainer.getUserName())
                                    .addParameter(CACTUS_USER_ID, UserContainer.getUserId())
                                    .addParameter(Q_REGISTRY_KEY, input.getParameter(FAKE_Q_REGISTRY_KEY));
                        }
                    }).toList());
        }
        return urls;
    }

    @RequestMapping(value = "/doubleOrHalveWeight", method = RequestMethod.POST)
    @JsonBody
    public Object doubleOrHalfWeight(@RequestParam("group") String group,
            @RequestParam("url") String urlStr, @RequestParam("operationType") int operationType) {
        URL url = URL.valueOf(urlStr).addParameter(Q_REGISTRY_KEY, group);
        ServiceSign sign = ServiceSign.makeServiceSign(url);
        String zkName = getZKName(sign.getZkId());
        if (Strings.isNullOrEmpty(zkName)) {
            return errorJson("无效的zk id");
        }

        if (operationType != ConstantHelper.WEIGHT_TYPE_DOUBLE && operationType != ConstantHelper.WEIGHT_TYPE_HALF) {
            logger.warn("invalid operation type [{}]", operationType);
            return errorJson("无效的操作类型");
        }

        String operateStr = operationType == ConstantHelper.WEIGHT_TYPE_DOUBLE ? "double weight" : "halve weight";
        logger.info("{} {}", operateStr, sign);
        try {
            weightConfigService.doubleOrHalfWeight(sign, url, operationType);
            eventBus.post(new UserOperationEvent(
                    UserContainer.getUserId(),
                    sign,
                    zkName + operateStr + ": serviceKey = [" + sign.getServiceKey() + "]"));
            return true;
        } catch (IllegalArgumentException e) {
            logger.warn("The weight may be less than 1", e);
            return errorJson("达到最小权重，无法执行半权操作！");
        } catch (Exception e) {
            logger.error("occur error when {} {}", operateStr, sign, e);
            return errorJson(buildVariousExceptionInfo(e));
        }
    }

    @RequestMapping(value = "/setMock", method = RequestMethod.POST)
    @JsonBody
    public Object setMock(@RequestParam(value = "id", required = false, defaultValue = EMPTY_ID + "") long id,
            @RequestParam("group") String group,
            @RequestParam("serviceKey") String serviceKey, @RequestParam("address") String address,
            @RequestParam("applicationName") String applicationName, @RequestParam("mockType") String mockType,
            @RequestParam("zkId") long zkId) {
        MockEnum mock;
        try {
            mock = MockEnum.makeMockEnum(mockType);
        } catch (IllegalArgumentException e) {
            logger.warn("do not support mock type {}", mockType);
            return errorJson("不支持的mock类型");
        }

        ServiceSign sign = ServiceSign.makeServiceSign(zkId, group, address, serviceKey);
        logger.info("set mock {} for app={}, {}", mock.getText(), applicationName, sign);

        String zkName = getZKName(sign.getZkId());
        if (Strings.isNullOrEmpty(zkName)) {
            return errorJson("无效的zk id");
        }

        if (id == EMPTY_ID) {
            try {
                mockConfigService.setMock(sign, applicationName, mock);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        sign,
                        zkName + "mock " + mock.getText() + ": serviceKey = [" + sign.getServiceKey() + "]"));
            } catch (Exception e) {
                logger.error("occur error when shield app={}, {}", applicationName, sign, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        } else {
            GovernanceData governanceData = queryGovernanceService.getGovernanceDataById(id);
            String result = getCheckDataResult(governanceData);
            if (!result.equals(VALID_DATA)) {
                return errorJson(result);
            }

            try {
                mockConfigService.changeMock(governanceData, mock);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        sign,
                        zkName + "mock " + mock.getText() + ": serviceKey = [" + sign.getServiceKey() + "]"));
            } catch (Exception e) {
                logger.error("occur error when shield app={}, {}", applicationName, governanceData, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        }
        return true;
    }

    @RequestMapping("/loadBalancePageRender")
    public String loadBalancePageRender(
            @RequestParam(value = "id", required = false, defaultValue = EMPTY_ID + "") long id,
            @RequestParam("group") String group,
            @RequestParam(value = "ipAndPort", required = false, defaultValue = "") String ipAndPort,
            @RequestParam("serviceGroup") String serviceGroup, @RequestParam("version") String version,
            @RequestParam("service") String service, @RequestParam("zkId") long zkId, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("group", group);
        model.addAttribute("ipAndPort", ipAndPort);
        model.addAttribute("serviceGroup", serviceGroup);
        model.addAttribute("version", version);
        model.addAttribute("service", service);
        model.addAttribute(ConstantHelper.ZKID, zkId);

        ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, null, serviceGroup, service, version);
        String loadBalance = loadBalanceService.getZkIdServiceKeyMatchedLoadBalance(serviceSign);
        if (id != EMPTY_ID) {
            GovernanceData governanceData = queryGovernanceService.getGovernanceDataById(id);
            if (governanceData != null) {
                loadBalance = governanceData.getUrl().getParameter(Constants.LOADBALANCE_KEY, "random");
            }
        }
        model.addAttribute("loadBalance", loadBalance);

        Set<String> availableLoadBalances = loadBalanceService.getAvailableLoadBalances(serviceSign);
        model.addAttribute("loadbalanceList", Lists.newArrayList(availableLoadBalances));
        return "configurator/loadBalanceEditor";
    }

    @RequestMapping(value = "/loadBalance", method = RequestMethod.POST)
    @JsonBody
    public Object loadBalance(@RequestParam(value = "id", required = false, defaultValue = EMPTY_ID + "") long id,
            @RequestParam("group") String group,
            @RequestParam("serviceGroup") String serviceGroup, @RequestParam("version") String version,
            @RequestParam("service") String service, @RequestParam("loadBalance") String loadBalance,
            @RequestParam("zkId") long zkId) {
        String address = Constants.ANYHOST_VALUE;
        ServiceSign sign = ServiceSign.makeServiceSign(zkId, group, address, serviceGroup, service, version);
        logger.info("set load balance {}, {}", loadBalance, sign);
        String zkName = getZKName(sign.getZkId());
        if (Strings.isNullOrEmpty(zkName)) {
            return errorJson("无效的zk id");
        }

        if (id == EMPTY_ID) {
            try {
                loadBalanceConfigService.setLoadBalance(sign, loadBalance);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        sign,
                        zkName + "调整负载均衡策略[" + loadBalance + "]: serviceKey = [" + sign.getServiceKey() + "]"));
            } catch (Exception e) {
                logger.error("occur error when set load balance {}, {}", loadBalance, sign, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        } else {
            GovernanceData governanceData = null;
            try {
                governanceData = queryGovernanceService.getGovernanceDataById(id);
                String result = getCheckDataResult(governanceData);
                if (!result.equals(VALID_DATA)) {
                    return errorJson(result);
                }
                loadBalanceConfigService.changeLoadBalance(governanceData, loadBalance);
                eventBus.post(new UserOperationEvent(
                        UserContainer.getUserId(),
                        sign,
                        zkName + "调整负载均衡策略[" + loadBalance + "]: serviceKey = [" + sign.getServiceKey() + "]"));
            } catch (Exception e) {
                logger.error("occur error when set load balance {}, {}", loadBalance, governanceData, e);
                return errorJson(buildVariousExceptionInfo(e));
            }
        }
        return true;
    }

    @RequestMapping("/delete")
    @JsonBody
    public Object delete(@RequestParam("group") String group, @RequestBody GovernanceData governanceData) {
        if (governanceData.getStatus().code == Status.DEL.code) {
            logger.warn("data is deleted");
            return true;
        }

        logger.info("delete {}", governanceData);

        try {
            configGovernanceService.delete(governanceData);
        } catch (Exception e) {
            logger.error("occur error when delete {}", governanceData, e);
            return errorJson(buildVariousExceptionInfo(e));
        }

        return true;
    }
}
