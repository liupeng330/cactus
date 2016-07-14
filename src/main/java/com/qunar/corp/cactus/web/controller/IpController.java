package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.Pair;
import com.qunar.corp.cactus.web.model.Provider;
import com.qunar.corp.cactus.web.util.UrlConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Comparator;

/**
 * Date: 13-11-5 Time: 下午9:01
 *
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/ip")
public class IpController extends AbstractController {

    @Resource
    private ProviderService providerService;

    @SuppressWarnings("unchecked")
    @RequestMapping("/showIpAndPort")
    public String showIpAndPort(@RequestParam(value = "group") final String group,
            @RequestParam(value = "machine", required = false, defaultValue = "") String machine, Model model) {
        model.addAttribute("group", group);

        Predicate<Pair<Provider, Integer>> machinePredicate = Strings.isNullOrEmpty(machine) ?
                Predicates.<Pair<Provider, Integer>> alwaysTrue() : new MachinePredicate(machine);
        model.addAttribute("list", providerService.distinctWith(ADDRESS_COMPARATOR).getItems(group).transform(
                new Function<URL, Pair<Provider, Integer>>() {
                    @Override
                    public Pair<Provider, Integer> apply(URL input) {
                        Pair<URL, Integer> result = Pair.makePair(input, Integer.parseInt(input.getParameter(ConstantHelper.CACTUS_ONLINE_STATUS)));
                        return new Pair<Provider, Integer>(UrlConverter.getProviderFromUrl(result.left, group), result.right);
                    }
                }
        ).filter(machinePredicate).toList());
        return "ip/showIpAndPort";
    }

    private final class MachinePredicate implements Predicate<Pair<Provider, Integer>> {

        private final String machine;

        MachinePredicate(String machine) {
            this.machine = Strings.nullToEmpty(machine).trim();
        }

        @Override
        public boolean apply(Pair<Provider, Integer> input) {
            return input.left.getAddress().startsWith(machine) || input.left.getHostNameAndPort().startsWith(machine);
        }
    }

    private static final Comparator<URL> ADDRESS_COMPARATOR = new Comparator<URL>() {
        @Override
        public int compare(URL lhs, URL rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            } else if (lhs == null) {
                return -1;
            } else if (rhs == null) {
                return 1;
            } else {
                return lhs.getAddress().compareTo(rhs.getAddress());
            }
        }
    };
}
