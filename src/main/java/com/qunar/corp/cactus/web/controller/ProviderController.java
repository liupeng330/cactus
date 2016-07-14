package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.util.PathUtil;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.web.util.UrlConverter;
import com.qunar.corp.cactus.util.UrlHelper;
import com.qunar.corp.cactus.web.model.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Comparator;
import java.util.List;

/**
 * Date: 13-10-29 Time: 下午2:53
 *
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/provider")
public class ProviderController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @Resource
    private ProviderService providerService;

    @RequestMapping("/showDetailByUrl")
    public String showDetailByUrl(@RequestParam("group") String group, @RequestParam("url") String url, Model model) {
        try {
            model.addAttribute("provider",
                    new UrlConverter.URLToProviderFunction(group).apply(URL.valueOf(URLDecoder.decode(url, "UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }
        return "provider/showDetail";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/showProviders")
    public String showProviders(@RequestParam("group") String group,
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam(value = "ipAndPort", required = false, defaultValue = "") String ipAndPort,
            @RequestParam("zkId") long zkId, Model model) {
        model.addAttribute("group", group);
        model.addAttribute("ipAndPort", ipAndPort);
        model.addAttribute("serviceKey", serviceKey);
        model.addAttribute(ConstantHelper.ZKID, zkId);

        try {
            ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, ipAndPort, serviceKey);
            List<URL> result = !Strings.isNullOrEmpty(ipAndPort) ?
                    providerService
                            .getConfiguredProviders(serviceSign.getGroup(), serviceSign.serviceInterface, UrlHelper.zkIdEqual(zkId),
                                    ProviderPredicates.addressEqual(serviceSign.getAddress()),
                                    ProviderPredicates.serviceKeyEqual(serviceSign))
                            .transform(UrlConverter.providerUrlToShow()).toList() :
                    providerService
                            .getConfiguredProviders(serviceSign.getGroup(), serviceSign.getServiceInterface(), UrlHelper.zkIdEqual(zkId),
                                    ProviderPredicates.serviceKeyEqual(serviceSign))
                            .transform(UrlConverter.providerUrlToShow()).toList();

            model.addAttribute("list", FluentIterable.from(UrlConverter.convertUrlsToProviders(group, result))
                    .toSortedList(new Comparator<Provider>() {
                        @Override
                        public int compare(Provider lhs, Provider rhs) {
                            return rhs.getParams().get(Constants.DISABLED_KEY).compareTo(
                                    lhs.getParams().get(Constants.DISABLED_KEY));
                        }
                    }));
        } catch (Exception e) {
            logger.error("error occurred when query provider, group=[{}] and serviceKey=[{}] and address=[{}]",
                    group, serviceKey, ipAndPort, e);
            return "redirect:/500.jsp";
        }
        return "provider/showProviders";

    }
}
