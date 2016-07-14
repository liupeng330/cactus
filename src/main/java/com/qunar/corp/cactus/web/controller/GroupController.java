package com.qunar.corp.cactus.web.controller;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.web.model.GroupRelationship;
import com.qunar.corp.cactus.service.graph.GraphService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.web.model.ListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Date: 13-10-28 Time: 下午4:05
 * 
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/group")
public class GroupController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Resource
    private GraphService graphService;

    @RequestMapping("/showGroup")
    public String showGroup(
            @RequestParam(value = "showMachine", required = false, defaultValue = "false") boolean showMachine,
            @RequestParam(value = "showByOwner", required = false, defaultValue = "false") boolean showByOwner,
            @RequestParam(value = "group", required = false, defaultValue = "") String group,
            @RequestParam(value = "machine", required = false, defaultValue = "") String machine,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = ListResult.DEFAULT_PAGE_SIZE) int pageSize,
            Model model) {
        String owner = UserContainer.getUserName();
        machine = machine.trim();
        group = group.trim();

        model.addAttribute("showMachine", showMachine);
        model.addAttribute("machine", machine);
        model.addAttribute("showByOwner", showByOwner);
        model.addAttribute("group", group);
        try {
            if (isSearchByMachine(machine)) {
                Set<String> addresses = Sets.newHashSet();
                addresses.addAll(graphService.searchIp(machine));
                for (String hostname : graphService.searchHostname(machine)) {
                    String ip = graphService.transformHostnamePortToIpPort(hostname);
                    if (!Strings.isNullOrEmpty(ip)) {
                        addresses.add(ip);
                    }
                }
                Set<String> foundGroup = Sets.newHashSet();
                for (String address : addresses) {
                    foundGroup.addAll(graphService.getAddressBelongGroups(address));
                }
                model.addAttribute("searchType", ConstantHelper.MACHINE_SEARCH_TYPE);
                model.addAttribute("data", machine);
                model.addAttribute("list", pageResult(pageNum, pageSize, Lists.newArrayList(foundGroup)));

            } else if (isShowByOwner(group, showByOwner)) {
                model.addAttribute("list",
                        pageResult(pageNum, pageSize, Lists.newArrayList(graphService.getGroupsByOwner(owner))));
            } else if (isSearchByOwner(group, showByOwner)) {
                final String lowerCaseGroup = group.toLowerCase();
                model.addAttribute(
                        "list",
                        pageResult(pageNum, pageSize, FluentIterable.from(graphService.getGroupsByOwner(owner))
                                .filter(new Predicate<String>() {
                                    @Override
                                    public boolean apply(String input) {
                                        return input.trim().toLowerCase().contains(lowerCaseGroup);
                                    }
                                }).toList()));
            } else if (isSearchFromAll(group, showByOwner)) {
                model.addAttribute("searchType", ConstantHelper.GROUP_SEARCH_TYPE);
                model.addAttribute("data", group);
                model.addAttribute("list", pageResult(pageNum, pageSize, graphService.searchGroups(group)));
            } else if (isShowAll(group, showByOwner)) {
                model.addAttribute("list", pageResult(pageNum, pageSize, graphService.getAllGroups()));
            }
            return "group/showGroup";
        } catch (Throwable e) {
            logger.error("occur error when show group with group [{}], show by owner flag [{}]", e);
            return "redirect:/500.jsp";
        }
    }

    private boolean isShowByOwner(String group, boolean showByOwner) {
        return Strings.isNullOrEmpty(group) && showByOwner;
    }

    private boolean isSearchByOwner(String group, boolean showByOwner) {
        return !Strings.isNullOrEmpty(group) && showByOwner;
    }

    private boolean isShowAll(String group, boolean showByOwner) {
        return Strings.isNullOrEmpty(group) && !showByOwner;
    }

    private boolean isSearchFromAll(String group, boolean showByOwner) {
        return !Strings.isNullOrEmpty(group) && !showByOwner;
    }

    private boolean isSearchByMachine(String machine) {
        return !Strings.isNullOrEmpty(machine);
    }

    @RequestMapping("/showRelationship")
    public String showGroupRelationship(@RequestParam("group") String group, Model model) {
        model.addAttribute("groupRelationship", new GroupRelationship(group, graphService.getRelayOn(group),
                graphService.getRelayMe(group)));
        return "group/showRelationship";
    }

}
