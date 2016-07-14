package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.RpcException;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.bean.IpAndPort;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.monitor.SystemCounter;
import com.qunar.corp.cactus.service.ZKClusterService;
import com.qunar.corp.cactus.service.mock.MockConsumerService;
import com.qunar.corp.cactus.service.mock.exception.FailGetInvokerException;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.PathUtil;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;
import com.qunar.corp.cactus.web.model.Address;
import com.qunar.corp.cactus.web.model.RpcInvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.AppServer;
import qunar.management.ServerManager;
import qunar.tc.qpt.dto.PressureParam;
import qunar.tc.qpt.dto.ServiceSignDto;
import qunar.tc.qpt.service.PressureTestService;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2014 2014/9/15 1:47
 */
@Controller
public class MockConsumerController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(MockConsumerController.class);

    @Resource
    private ProviderService providerService;
    @Resource
    private PressureTestService pressureTestService;
    @Resource
    private MockConsumerService mockConsumerService;

    @Resource
    private ZKClusterService zkClusterService;

    @SuppressWarnings("unchecked")
    @RequestMapping("/mockConsumerConfig")
    public String mockConsumerConfig(@RequestParam("group") String group,
                                     @RequestParam("ipAndPort") String address,
                                     @RequestParam("service") String service,
                                     @RequestParam("serviceGroup") String serviceGroup,
                                     @RequestParam("version") String version,
                                     @RequestParam("zkId") long zkId,
                                     @RequestParam("pef") int needPefTest,
                                     Model model) {
        if (!isLegalEnv()) {
            return "redirect:/403.jsp";
        }

        try {
            ServiceSign sign = ServiceSign.makeServiceSign(zkId, group, address, serviceGroup, service, version);

            Predicate<URL> predicate = Predicates.and(
                    UrlHelper.zkIdEqual(zkId),
                    ProviderPredicates.serviceKeyEqual(sign));
            if (sign.hasAddress()) {
                predicate = Predicates.and(predicate, ProviderPredicates.addressEqual(sign.getAddress()));
            }
            FluentIterable<URL> providers = providerService.getItems(sign.getGroup(), sign.getServiceInterface(), predicate);

            Set<String> methods = Sets.newHashSet();
            Set<String> providerIpPorts = Sets.newHashSetWithExpectedSize(providers.size() + 1);
            for (URL provider : providers) {
                methods.addAll(UrlHelper.getMethods(provider));
                providerIpPorts.add(provider.getAddress());
            }
            List<Address> providerAddresses = Lists.newArrayListWithCapacity(providerIpPorts.size());
            for (String ipPort : providerIpPorts) {
                try {
                    IpAndPort ipAndPort = IpAndPort.fromString(ipPort);
                    Address providerAddress = new Address(ipAndPort.getIp(), CommonCache.getHostNameByIp(ipAndPort.getIp()), ipAndPort.getPort());
                    providerAddresses.add(providerAddress);
                } catch (Exception e) {
                    logger.warn("can not resolve address, address={}, group={}, serviceGroup={}, service={}, version={}, zkId={}",
                            ipPort, group, serviceGroup, service, version, zkId, e);
                }
            }

            model.addAttribute("sign", sign);
            model.addAttribute("methods", methods);
            if (methods.isEmpty()) {
                model.addAttribute("defaultMethod", "");
            } else {
                model.addAttribute("defaultMethod", methods.iterator().next());
            }

            model.addAttribute("providerAddresses", providerAddresses);
            if (providerAddresses.isEmpty()) {
                model.addAttribute("defaultProviderAddress", new Address("", "", 0));
            } else {
                model.addAttribute("defaultProviderAddress", providerAddresses.iterator().next());
            }
            if (needPefTest == 0)
                return "mock/mockConfig";
            else
                return "mock/performanceTest";
        } catch (Exception e) {
            logger.error("mock consumer config error, group={}, address={}, serviceGroup={}, service={}, version={}, zkId={}",
                    group, address, serviceGroup, service, version, zkId, e);
            return "redirect:/500.jsp";
        }
    }

    private boolean isLegalEnv() {
        return ServerManager.getInstance().getAppConfig().getServer().getType() != AppServer.Type.prod;
    }

    @RequestMapping("/mock/invoke")
    @JsonBody
    public Object doInvoke(@RequestParam("group") String group,
                           @RequestParam("ipAndPort") String address,
                           @RequestParam("service") String service,
                           @RequestParam("serviceGroup") String serviceGroup,
                           @RequestParam("version") String version,
                           @RequestParam("zkId") long zkId,
                           @RequestParam("method") String method,
                           @RequestParam("parameter") String parameter,
                           @RequestParam("needPerformanceTest") int needPerformanceTest,
                           @RequestParam("requestThreads") int threads,
                           @RequestParam("totalQps") int qpsTotal,
                           @RequestParam("requestPerThreds") int requestPerThread,
                           @RequestParam("totalTime") int totalTime,
                           @RequestParam("qpsPerThreads") int qpsPerThread,
                           @RequestParam("timeOut") int timeOut,
                           @RequestParam("dataSelectPolicy") int dataSelectPolicy,
                           @RequestParam("pefTestData") String pefTestData
    ) {
        if (!isLegalEnv()) {
            return errorJson("没有权限");
        }

        SystemCounter.mockConsumerCount.increment();

        try {
            logger.info("mock consumer invoke, group={}, address={}, serviceGroup={}, service={}, " +
                            "version={}, zkId={}, method={}, parameter={}", group, address, serviceGroup, service,
                    version, zkId, method, parameter);

            ServiceSign sign = ServiceSign.makeServiceSign(zkId, group, address, serviceGroup, service, version);
            Preconditions.checkArgument(!Strings.isNullOrEmpty(sign.getAddress()), "address can not be null or empty");
            Preconditions.checkArgument(zkClusterService.findZkCluster(zkId).isPresent(), "invalid zk id");
            parameter = Strings.nullToEmpty(parameter).trim();
            Preconditions.checkArgument(Strings.isNullOrEmpty(parameter) || isJson(parameter), "invalid parameter");

            RpcInvokeResult result = mockConsumerService.invoke(sign, method, parameter);
            if (needPerformanceTest == 0)
                return result;
            //以下为性能测试
            PressureParam pressureParam = new PressureParam(timeOut, threads, qpsTotal, requestPerThread, qpsPerThread, totalTime,dataSelectPolicy==0? PressureParam.DataSelectPolicy.ROUND_ROBIN: PressureParam.DataSelectPolicy.RANDOM);
            ServiceSignDto dto = new ServiceSignDto(PathUtil.getGroup(group), PathUtil.getMidPath(group), address, serviceGroup, service, version);
            pressureTestService.invoke(dto, method, mockConsumerService.getParaTypes(parameter), mockConsumerService.coneverStringToData(pefTestData),pressureParam, UserContainer.getUserName());

            return result;

        } catch (IllegalArgumentException e) {
            return errorJson(e.getMessage());
        } catch (FailGetInvokerException e) {
            return errorJson("can not get provider: " + e.getMessage());
        } catch (RpcException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("rpc exception: ");
            sb.append(e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                sb.append("...\r\n");
                sb.append("caused by ");
                sb.append(cause.getClass().getName());
                sb.append(": ");
                sb.append(cause.getMessage());
            }
            return errorJson(sb.toString());
        } catch (Exception e) {
            logger.error("occur error when do invoke, group={}, address={}, serviceGroup={}, service={}, " +
                            "version={}, zkId={}, method={}, parameter={}", group, address, serviceGroup, service,
                    version, zkId, method, parameter, e);
            return errorJson(e.getMessage());
        }
    }

    private boolean isJson(String body) {
        return body.startsWith("{") && body.endsWith("}");
    }
}
