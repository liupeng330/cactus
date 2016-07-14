/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web.controller;

import com.google.common.base.Strings;
import com.qunar.corp.cactus.exception.GroupClusterExistException;
import com.qunar.corp.cactus.service.NodeRelationService;
import com.qunar.corp.cactus.service.SpecialService;
import com.qunar.corp.cactus.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;

/**
 * @author zhenyu.nie created on 2014 14-3-19 下午7:28
 */
@Controller
@RequestMapping("/special")
public class SpecialController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(SpecialController.class);

    @Resource
    private SpecialService specialService;

    @Resource(name = "notifyService")
    private NodeRelationService notifyService;

    @RequestMapping(value = "/showSpecials", method = RequestMethod.GET)
    public String showSpecials(Model model) {
        model.addAttribute("specialList", specialService.getGroupClusters());
        return "special/showSpecial";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @JsonBody
    public Object delete(@RequestParam("cluster") String cluster) {
        logger.info("delete special, cluster={}", cluster);
        specialService.delete(cluster);
        fresh();
        return "删除成功";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @JsonBody
    public Object add(@RequestParam("path") String path) {
        logger.info("add special path, path={}", path);
        try {
            path = Strings.nullToEmpty(path).trim();
            if (path.isEmpty()) {
                return errorJson("无效的路径");
            }

            String cluster;
            String midPath;
            try {
                cluster = PathUtil.getGroup(path);
                midPath = PathUtil.getMidPath(path);
            } catch (Exception e) {
                return errorJson("无效的路径");
            }

            if (cluster.isEmpty() || !midPath.isEmpty()) {
                return errorJson("无效的路径");
            }

            specialService.add(cluster);
            fresh();
            return "添加成功!";
        } catch (GroupClusterExistException e){
            return errorJson("该路径已存在，无法重复添加!");
        } catch (Throwable e) {
            logger.error("add special monitor error with path {}", path, e);
            return errorJson(e.getMessage());
        }
    }

    private void fresh() {
        try {
            notifyService.fresh();
        } catch (RuntimeException e) {
            logger.error("notify special group change error", e);
        }
    }
}
