package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.common.base.Strings;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.PathType;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.util.ConstantHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.dubbo.common.Constants.*;

/**
 * Date: 13-12-18 Time: 下午1:33
 * 
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/distribute")
public class DistributeController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(DistributeController.class);

    @Resource
    private IQueryGovernanceService queryGovernanceService;

    @RequestMapping("/distributeFromGroup")
    public ModelAndView distributeFromGroup(
            @RequestParam(value = "showMachine", required = false, defaultValue = "false") boolean showMachine,
            @RequestParam("group") String group,
            @RequestParam(value = "machine", required = false, defaultValue = "") String machine,
            HttpServletRequest request) {
        String redirectPage = request.getContextPath() + "/serviceSign/showServiceSign";
        if (showMachine) {
            redirectPage = request.getContextPath() + "/ip/showIpAndPort";
        }
        RedirectView redirectView = getRedirectView(redirectPage);
        redirectView.addStaticAttribute("group", group);
        if (!Strings.isNullOrEmpty(machine)) {
            redirectView.addStaticAttribute("machine", machine);
        }
        return new ModelAndView(redirectView);
    }

    @RequestMapping("/distributeToUpdate")
    public ModelAndView distributeToUpdate(@RequestParam("id") long id, HttpServletRequest request) {
        GovernanceData governanceData = queryGovernanceService.getGovernanceDataById(id);
        if (governanceData == null) {
            return new ModelAndView("/500.jsp");
        }
        if (governanceData.getPathType().code == PathType.CONFIG.code) {
            if (governanceData.getUrl().getParameter(Constants.LOADBALANCE_KEY) != null) {
                return updateLoadBalancePage(governanceData, request);
            } else if (governanceData.getUrl().getParameter(Constants.MOCK_KEY) != null) {
                return updateMockPage(governanceData, request);
            } else {
                return updateConfigPage(governanceData, request);
            }
        } else {
            return updateRouterPage(governanceData, request);
        }
    }

    private ModelAndView updateLoadBalancePage(GovernanceData governanceData, HttpServletRequest request) {
        String redirectPage = request.getContextPath() + "/configurator/loadBalancePageRender";
        RedirectView redirectView = getRedirectView(redirectPage);
        redirectView.addStaticAttribute("id", governanceData.getId());
        redirectView.addStaticAttribute("group", governanceData.getGroup());
        redirectView.addStaticAttribute("ipAndPort", governanceData.getUrl().getAddress());
        redirectView.addStaticAttribute("serviceGroup", governanceData.getServiceGroup());
        redirectView.addStaticAttribute("version", governanceData.getVersion());
        redirectView.addStaticAttribute("service", governanceData.getServiceName());
        redirectView.addStaticAttribute("zkId", governanceData.getZkId());
        return new ModelAndView(redirectView);
    }

    private ModelAndView updateConfigPage(GovernanceData governanceData, HttpServletRequest request) {
        String redirectPage = request.getContextPath() + "/configurator/addPageRender";
        RedirectView redirectView = getRedirectView(redirectPage);
        redirectView.addStaticAttribute("name", governanceData.getName());
        redirectView.addStaticAttribute("id", governanceData.getId());
        redirectView.addStaticAttribute("group", governanceData.getGroup());
        redirectView.addStaticAttribute("serviceGroup", governanceData.getServiceGroup());
        redirectView.addStaticAttribute("service", governanceData.getServiceName());
        redirectView.addStaticAttribute("version", governanceData.getVersion());
        redirectView.addStaticAttribute("address", governanceData.getUrl().getAddress());
        redirectView.addStaticAttribute("params", getToUpdateParamsStr(governanceData.getUrl()));
        redirectView.addStaticAttribute("zkId", governanceData.getZkId());
        return new ModelAndView(redirectView);
    }

    private ModelAndView updateMockPage(GovernanceData governanceData, HttpServletRequest request) {
        String redirectPage = request.getContextPath() + "/configurator/updateMockRender";
        RedirectView redirectView = getRedirectView(redirectPage);
        redirectView.addStaticAttribute("id", governanceData.getId());
        return new ModelAndView(redirectView);
    }

    private ModelAndView updateRouterPage(GovernanceData governanceData, HttpServletRequest request) {
        String redirectPage = request.getContextPath() + "/router/addPageRender";
        RedirectView redirectView = getRedirectView(redirectPage);
        redirectView.addStaticAttribute("id", governanceData.getId());
        redirectView.addStaticAttribute("group", governanceData.getGroup());
        redirectView.addStaticAttribute("zkId", governanceData.getZkId());
        redirectView.addStaticAttribute("serviceKey", ServiceSign.makeServiceSign(governanceData.getFullUrl())
                .getServiceKey());
        return new ModelAndView(redirectView);
    }

    private RedirectView getRedirectView(String redirectPage) {
        RedirectView redirectView = new RedirectView(redirectPage);
        redirectView.setExpandUriTemplateVariables(false); // 要设置这个值为false，来避免group中带“{}”时会导致IllegalArgumentException
        return redirectView;
    }

    private String getToUpdateParamsStr(URL url) {
        Map<String, String> parameters = new HashMap<String, String>(url.getParameters());
        parameters.remove(CATEGORY_KEY);
        parameters.remove(ConstantHelper.ZKID);
        parameters.remove(ConstantHelper.CACTUS);
        parameters.remove(ConstantHelper.Q_REGISTRY_KEY);
        parameters.remove(VERSION_KEY);
        parameters.remove(ENABLED_KEY);
        parameters.remove(ConstantHelper.NAME);
        parameters.remove(DYNAMIC_KEY);
        parameters.remove(APPLICATION_KEY);
        parameters.remove(ConstantHelper.CACTUS_TIME);
        parameters.remove(ConstantHelper.CACTUS);
        parameters.remove(GROUP_KEY);
        return StringUtils.toQueryString(parameters).replace("&", "|");
    }
}
