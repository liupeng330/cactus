package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.drainage.service.DrainageManager;
import com.qunar.corp.cactus.service.governance.config.LoadBalanceConfigService;
import com.qunar.corp.cactus.service.graph.GraphService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.management.ServerManager;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date: 13-11-13 Time: 下午4:35
 *
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/serviceSign")
public class ServiceSignController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceSignController.class);

    @Resource
    private ProviderService providerService;

    @Resource
    private LoadBalanceConfigService loadBalanceService;

    @Resource
    private GraphService graphService;

    @RequestMapping("/search/showServiceSign")
    public String searchServiceSign(@RequestParam("interfaze") String interfaze, Model model) {
        try {
            interfaze = interfaze.trim();
            model.addAttribute("searchType", ConstantHelper.SERVICE_SEARCH_TYPE);
            model.addAttribute("data", interfaze);
            List<Pair<ServiceSign, Map<String, String>>> pairList = convertUrlsToServiceSignAndParamPair(transformInterfazes2Urls(Iterables.concat(
                    graphService.searchService(interfaze), graphService.searchServiceUseSimpleName(interfaze))));
            model.addAttribute("list", pairList);
            model.addAttribute("env", ServerManager.getInstance().getAppConfig().getServer().getType());

            addDrainageStatus(model, pairList);
            return "sk/showServiceSign";
        } catch (Exception e) {
            logger.error("An exception occurred when query serviceSign by interfaze[{}]!", interfaze, e);
            return "redirect:/500.jsp";
        }
    }

    @Resource
    private DrainageManager drainageManager;

    @SuppressWarnings("unchecked")
    @RequestMapping("/showServiceSign")
    public String showServiceSign(@RequestParam(value = "group") String group,
                                  @RequestParam(value = "ipAndPort", required = false, defaultValue = "") String ipAndPort, Model model) {
        model.addAttribute("group", group);
        model.addAttribute("ipAndPort", ipAndPort);

        Predicate<URL> ipAndPortPredicate = Strings.isNullOrEmpty(ipAndPort) ? Predicates.<URL>alwaysTrue() : new ProviderPredicates.AddressPredicate(ipAndPort);
        try {
            List<Pair<ServiceSign, Map<String, String>>> pairList = convertUrlsToServiceSignAndParamPair(providerService
                    .distinctWith(SERVICE_KEY_COMPARATOR)
                    .distinctWith(ZKID_COMPARATOR)
                    .getItems(group, ipAndPortPredicate).toList());
            model.addAttribute("list", pairList);
            model.addAttribute("env", ServerManager.getInstance().getAppConfig().getServer().getType());

            addDrainageStatus(model, pairList);

            return "sk/showServiceSign";
        } catch (Exception e) {
            logger.error("occur error when show serviceSign, group={}, ipAndPort={}", group, ipAndPort, e);
            return "redirect:/500.jsp";
        }
    }

    private void addDrainageStatus(Model model, List<Pair<ServiceSign, Map<String, String>>> pairList) {
        Map<String, Boolean> drainageStatusMap = Maps.newHashMap();
        for (Pair<ServiceSign, Map<String, String>> serviceSignMapPair : pairList) {
            String serviceInterface = serviceSignMapPair.getLeft().getServiceInterface();
            drainageStatusMap.put(serviceInterface, drainageManager.serviceIsDrainaging(serviceInterface));
        }
        model.addAttribute("drainageStatusMap", drainageStatusMap);
    }

    private List<Pair<ServiceSign, Map<String, String>>> convertUrlsToServiceSignAndParamPair(List<URL> urls) {
        return FluentIterable.from(urls).transform(new Function<URL, Pair<ServiceSign, Map<String, String>>>() {
            @Override
            public Pair<ServiceSign, Map<String, String>> apply(URL input) {
                ServiceSign serviceSign = ServiceSign.makeServiceSign(input);
                Map<String, String> param = Maps.newHashMap();
                param.put(ConstantHelper.CACTUS_ONLINE_STATUS, input.getParameter(ConstantHelper.CACTUS_ONLINE_STATUS));
                param.put(Constants.LOADBALANCE_KEY, loadBalanceService.getZkIdServiceKeyMatchedLoadBalance(serviceSign));
                param.put(ConstantHelper.ZKID, input.getParameter(ConstantHelper.ZKID));
                param.put(ConstantHelper.ZK_NAME, getZKName(Long.parseLong(input.getParameter(ConstantHelper.ZKID))));
                return new Pair<ServiceSign, Map<String, String>>(serviceSign, param);
            }
        }).toList();
    }

    @SuppressWarnings("unchecked")
    private List<URL> transformInterfazes2Urls(Iterable<String> interfazes) {
        Map<String, Set<String>> groupInterfaceMapping = getGroupInterfaceMapping(interfazes);
        List<URL> urls = Lists.newArrayList();
        for (Map.Entry<String, Set<String>> groupInterfacesEntry : groupInterfaceMapping.entrySet()) {
            String group = groupInterfacesEntry.getKey();
            Set<String> interfaces = groupInterfacesEntry.getValue();
            urls.addAll(providerService
                    .distinctWith(SERVICE_KEY_COMPARATOR)
                    .distinctWith(ZKID_COMPARATOR)
                    .getItems(group, serviceNameIn(interfaces)).toList());
        }
        return urls;
    }

    private Map<String, Set<String>> getGroupInterfaceMapping(Iterable<String> interfazes) {
        Map<String, Set<String>> groupInterfaceMapping = Maps.newHashMap();
        for (String interfaze : interfazes) {
            Set<String> groups = graphService.getGroupsByService(interfaze);
            for (String group : groups) {
                Set<String> interfacesByGroup = groupInterfaceMapping.get(group);
                if (interfacesByGroup == null) {
                    interfacesByGroup = Sets.newHashSet();
                    groupInterfaceMapping.put(group, interfacesByGroup);
                }
                interfacesByGroup.add(interfaze);
            }
        }
        return groupInterfaceMapping;
    }

    private static final Comparator<URL> ZKID_COMPARATOR = new Comparator<URL>() {
        @Override
        public int compare(URL lhs, URL rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            } else if (lhs == null) {
                return -1;
            } else if (rhs == null) {
                return 1;
            } else {
                return lhs.getParameter(ConstantHelper.ZKID).compareTo(rhs.getParameter(ConstantHelper.ZKID));
            }
        }
    };

    private static final Comparator<URL> SERVICE_KEY_COMPARATOR = new Comparator<URL>() {
        @Override
        public int compare(URL lhs, URL rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            } else if (lhs == null) {
                return -1;
            } else if (rhs == null) {
                return 1;
            } else {
                return lhs.getServiceKey().compareTo(rhs.getServiceKey());
            }
        }
    };

    private Predicate<URL> serviceNameIn(final Set<String> interfaces) {
        return new Predicate<URL>() {
            @Override
            public boolean apply(URL input) {
                return interfaces.contains(input.getServiceInterface());
            }
        };
    }
}
