package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.PathUtil;
import com.qunar.corp.cactus.util.UrlHelper;
import com.qunar.corp.cactus.web.util.UrlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Date: 13-10-29 Time: 下午2:53
 *
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/consumer")
public class ConsumerController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Resource
    private ConsumerService consumerService;

    @SuppressWarnings("unchecked")
    @RequestMapping("/showConsumers")
    public String showConsumers(@RequestParam("group") String group,
            @RequestParam("serviceKey") String serviceKey, @RequestParam("zkId") long zkId,
            // 此参数用于从consumer页面跳转到provider页面时，让providerService识别是否根据ipAndPort查找providers
            @RequestParam(value = "ipAndPort", required = false, defaultValue = "") String ipAndPort,
            Model model) {
        model.addAttribute("group", group);
        model.addAttribute("ipAndPort", ipAndPort);
        model.addAttribute("serviceKey", serviceKey);
        model.addAttribute(ConstantHelper.ZKID, zkId);
        model.addAttribute("list", UrlConverter.convertUrlsToConsumers(
                group,
                consumerService.getZkIdAndServiceKeyMatchedConfiguredConsumers(
                        ServiceSign.makeServiceSign(zkId, group, serviceKey)).toSortedList(new Comparator<URL>() {
                                @Override
                                public int compare(URL o1, URL o2) {
                                    return getMockRankWeight(o1) - getMockRankWeight(o2);
                                }
                })));
        return "consumer/showConsumers";
    }

    // todo: 这里几个数字
    private int getMockRankWeight(URL configuredConsumerUrl) {
        String mock = configuredConsumerUrl.getParameter(Constants.MOCK_KEY, "").isEmpty() ? configuredConsumerUrl
                .getParameter(ConstantHelper.MOCK_KEY_ON_CONFIG_URL) : configuredConsumerUrl.getParameter(
                Constants.MOCK_KEY, "");
        if (Strings.isNullOrEmpty(mock)) {
            return 4;
        } else if (mock.equals("true") || mock.equals("return+null")) {// 默认容错
            return 3;
        } else if (mock.equals("fail:return+null")) { // 容错
            return 2;
        } else { // 屏蔽或非法参数，如果有设置了mock值为非法的值，则首先排在前面
            return 1;
        }
    }

    @RequestMapping("/showDetail")
    public String showDetail(@RequestParam("group") String group, @RequestParam("encodeUrl") String encodeUrl,
            Model model) {
        try {
            model.addAttribute("consumer", new UrlConverter.URLToConsumerFunction(group).apply(URL.valueOf(URLDecoder
                    .decode(encodeUrl, "UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }
        return "consumer/showDetail";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/showAppConsumers")
    @JsonBody
    public Object showApplicationMachines(@RequestParam("group") String group, @RequestParam("app") String app) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(group), "group不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(app), "app不能为空");

        Set<String> consumerAddresses = consumerService
                .getItems(group, UrlHelper.hasKeyValue(Constants.APPLICATION_KEY, app))
                .transform(UrlHelper.TO_ADDRESS_FUNC)
                .toSet();

        List<HostnameWithIp> hostnameWithIps = Lists.newArrayListWithCapacity(consumerAddresses.size());
        for (String consumerAddress : consumerAddresses) {
            hostnameWithIps.add(new HostnameWithIp(consumerAddress));
        }

        return Ordering.from(new Comparator<HostnameWithIp>() {
            @Override
            public int compare(HostnameWithIp lhs, HostnameWithIp rhs) {
                return lhs.hostname.compareTo(rhs.hostname);
            }
        }).immutableSortedCopy(hostnameWithIps);
    }

    public static class HostnameWithIp {
        private String hostname;
        private String ip;

        public HostnameWithIp(String ip) {
            this.ip = ip;
            this.hostname = CommonCache.getHostNameByIp(ip);
        }

        public String getHostname() {
            return hostname;
        }

        public String getIp() {
            return ip;
        }

        @Override
        public String toString() {
            return "HostnameWithIp{" +
                    "hostname='" + hostname + '\'' +
                    ", ip='" + ip + '\'' +
                    '}';
        }
    }
}
