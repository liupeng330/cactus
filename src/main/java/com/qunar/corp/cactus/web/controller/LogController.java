package com.qunar.corp.cactus.web.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.LogQuery;
import com.qunar.corp.cactus.web.model.ListResult;
import com.qunar.corp.cactus.service.LogService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Date: 13-11-10 Time: 下午5:57
 * 
 * @author: xiao.liang
 * @description:
 */
@Controller
@RequestMapping("/log")
public class LogController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Resource
    private LogService logService;

    @RequestMapping("/showByName")
    public String showByName(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = ListResult.DEFAULT_PAGE_SIZE) int pageSize,
            Model model) {
        String username = UserContainer.getUserName();
        HashMap<String, Object> param = Maps.newHashMap();
        param.put("username", username);
        model.addAttribute(
                "list",
                pageDatabaseResult(pageNum, pageSize, logService.getTotalSize(LogQuery.makeBuilder().username(username).build()),
                        logService.listByUserName(username, transformPageNum2Index(pageNum, pageSize), pageSize)));
        return "log/showByName";
    }

    @RequestMapping("/showByConditionRender")
    public String searchRender(Model model) {
        return "log/showByCondition";
    }

    @RequestMapping("/showByCondition")
    public String search(
            @RequestParam("group") String group,
            @RequestParam("service") String service,
            @RequestParam(value = "fromTime", required = false, defaultValue = "") String fromTime,
            @RequestParam(value = "toTime", required = false, defaultValue = "") String toTime,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = ListResult.DEFAULT_PAGE_SIZE) int pageSize,
            Model model) {
        HashMap<String, Object> param = Maps.newHashMap();
        LogQuery.Builder builder = LogQuery.makeBuilder();
        if (!Strings.isNullOrEmpty(group)) {
            builder.group(group.trim());
        }
        if (!Strings.isNullOrEmpty(service)) {
            builder.service(service.trim());
        }
        if (!Strings.isNullOrEmpty(fromTime)) {
            builder.fromTime(DateHelper.str2DateWithSecond(fromTime.trim()));
        }
        if (!Strings.isNullOrEmpty(toTime)) {
            builder.toTime(DateHelper.str2DateWithSecond(toTime.trim()));
        }
        LogQuery logQuery = builder.build();
        model.addAttribute("group", group);
        model.addAttribute("service", service);
        model.addAttribute("fromTime", fromTime);
        model.addAttribute("toTime", toTime);
        model.addAttribute(
                "list",
                pageDatabaseResult(pageNum, pageSize, logService.getTotalSize(logQuery),
                        logService.search(logQuery, transformPageNum2Index(pageNum, pageSize), pageSize)));
        return "log/showByCondition";
    }
}
