package com.qunar.corp.cactus.web.controller;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.service.graph.GraphService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.governance.router.RouterConstants;
import com.qunar.corp.cactus.service.governance.router.RouterHelper;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.web.model.GovernanceView;
import com.qunar.corp.cactus.web.model.ListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * Date: 13-10-29 Time: 下午2:53
 * 
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/search")
public class SearchController extends AbstractController {

    private static final int NON_STATUS = -1;

    private static final String FIND_ALL_SYMBOL = "*";

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private static final int SHOW_HINT_SIZE = 10;

    @Resource
    private IQueryGovernanceService queryGovernanceService;
    
    @Resource
    private GraphService graphService;

    @RequestMapping(value = "/searchInfo", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView searchService(
            @RequestParam("data") String data,
            @RequestParam("searchType") int type,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = ListResult.DEFAULT_PAGE_SIZE) int pageSize,
            HttpServletRequest request) {
        String redirectPage = request.getContextPath() + "/serviceSign/search/showServiceSign";
        if (type == ConstantHelper.SERVICE_SEARCH_TYPE) {
            RedirectView redirectView = new RedirectView(redirectPage);
            redirectView.setExpandUriTemplateVariables(false);
            redirectView.addStaticAttribute("interfaze", data);
            return new ModelAndView(redirectView);
        }
        if (type == ConstantHelper.GROUP_SEARCH_TYPE) {
            redirectPage = request.getContextPath() + "/group/showGroup";
            RedirectView redirectView = new RedirectView(redirectPage);
            redirectView.setExpandUriTemplateVariables(false);
            redirectView.addStaticAttribute("group", data);
            return new ModelAndView(redirectView);
        }
        if (type == ConstantHelper.MACHINE_SEARCH_TYPE) {
            redirectPage = request.getContextPath() + "/group/showGroup";
            RedirectView redirectView = new RedirectView(redirectPage);
            redirectView.setExpandUriTemplateVariables(false);
            redirectView.addStaticAttribute("machine", data);
            redirectView.addStaticAttribute("showMachine", true);
            return new ModelAndView(redirectView);
        }

        return new ModelAndView("/500.jsp");

    }

    @RequestMapping("/searchConfigOrRouter")
    public String searchConfigOrRouter(@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "serviceName", required = false) String serviceName,
            @RequestParam(value = "machine", required = false, defaultValue = "") String machine,
            @RequestParam(value = "status", required = false, defaultValue = "0") int status,
            @RequestParam("pathType") int pathType, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("group", group);
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("machine", machine);
        model.addAttribute("status", status);
        model.addAttribute("pathType", pathType);
        name = preprocessParam(name);
        group = preprocessParam(group);
        serviceName = preprocessParam(serviceName);
        machine = machine.trim();
        Status statusType = status == NON_STATUS ? null : Status.fromCode(status);

        if (Strings.isNullOrEmpty(machine)) {
            model.addAttribute(
                    "list",
                    queryGovernanceService
                            .getGovernanceDatas(
                                    GovernanceQuery.createBuilder().likeName(name).group(group)
                                            .likeServcieName(serviceName).pathType(PathType.fromCode(pathType))
                                            .status(statusType).dataType(DataType.USERDATA).build())
                            .transform(TO_GOVERNANCE_VIEW_FUNC).toList());

        } else {
            if (Strings.isNullOrEmpty(group)) {
                model.addAttribute("errorInfo", "按机器搜索时需要指定一个应用名！");
            } else {
                String ip = machine;
                boolean shouldSearch = true;
                if (!IpAndPort.isLegalIp(machine)) {
                    ip = CommonCache.getIpByHostname(machine);
                    if (ip.equals(machine)) {
                        model.addAttribute("errorInfo", "不正确的机器名或IP地址");
                        shouldSearch = false;
                    }
                }

                if (shouldSearch) {
                    model.addAttribute(
                            "list",
                            queryGovernanceService
                                    .getGovernanceDatas(
                                            GovernanceQuery.createBuilder().likeName(name).group(group)
                                                    .likeServcieName(serviceName).pathType(PathType.fromCode(pathType))
                                                    .status(statusType).dataType(DataType.USERDATA).build())
                                    .transform(TO_GOVERNANCE_VIEW_FUNC).filter(new IpInRulePredicate(ip)).toList());
                }
            }
        }
        return "search/searchConfigOrRouter";
    }

    @RequestMapping("/showGroupHint")
    @JsonBody
    public Object groupHint(@RequestParam("group") String group) {
        return limitResult(graphService.searchGroups(group), SHOW_HINT_SIZE);
    }

    @RequestMapping("/showServiceHint")
    @JsonBody
    public Object serviceHint(@RequestParam("service") String service) {
        List<String> result = graphService.searchServiceUseSimpleName(service);
        if (result.size() < SHOW_HINT_SIZE) {
            return Sets.newHashSet(Iterables.concat(result,
                    limitResult(graphService.searchService(service), SHOW_HINT_SIZE - result.size())));
        }
        return limitResult(result, SHOW_HINT_SIZE);
    }

    @RequestMapping("/showMachineHint")
    @JsonBody
    public Object machineHint(@RequestParam("machine") String machine) {
        return searchMachine(machine);
    }

    @RequestMapping("/showNoPortMachineHint")
    @JsonBody
    public Object noPortMachineHint(@RequestParam("machine") String machine) {
        return omitPort(searchMachine(machine));
    }

    private List<String> searchMachine(String machine) {
        List<String> result = graphService.searchIp(machine);
        if (result.size() < SHOW_HINT_SIZE) {
            return Lists.newArrayList(Iterables.concat(result,
                    limitResult(graphService.searchHostname(machine), SHOW_HINT_SIZE - result.size())));
        }
        return limitResult(result, SHOW_HINT_SIZE);

    }

    private Set<String> omitPort(List<String> data) {
        Set<String> result = Sets.newHashSet();
        for (String address : data) {
            int indexOfColon  = address.indexOf(":");
            if (indexOfColon < 0) {
                result.add(address);
            }
            result.add(address.substring(0, indexOfColon));
        }
        return result;
    }
    
    private List<String> limitResult(List<String> data, int limitSize) {
        if (data.size() <= limitSize) {
            return Lists.newArrayList(data);
        }
        return Lists.newArrayList(data.subList(0, limitSize));
    }


    private class IpInRulePredicate implements Predicate<GovernanceView> {

        private final String ip;
        
        private IpInRulePredicate(String ip) {
            this.ip = ip;
        }

        @Override
        public boolean apply(GovernanceView input) {
            return RouterHelper.isRouterMatch(input.getGovernanceData().getUrl(), RouterConstants.PROVIDER_HOST, ip);
        }
    }
    
    private static final Function<String, String> ADDRESS_2_IP_FUNC = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return IpAndPort.fromString(input).getIp();
        }
    };

    private String preprocessParam(String param) {
        if (Strings.isNullOrEmpty(param) || FIND_ALL_SYMBOL.equals(param)) {
            return null;
        }
        return param.trim().toLowerCase();
    }
}
