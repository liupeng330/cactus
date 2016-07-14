/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.web.controller;

import com.qunar.corp.cactus.bean.ZKCluster;
import com.qunar.corp.cactus.service.ZKClusterService;
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
 * @author zhenyu.nie created on 2013 13-11-1 下午1:51
 */
@Controller
@RequestMapping("/zkcluster")
public class ZkClusterController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ZkClusterController.class);

    private final static int CLUSTER_NAME_MAX_SIZE = 50;

    private final static int CLUSTER_ADDRESS_MAX_SIZE = 1000;

    @Resource
    private ZKClusterService zkClusterService;

    @RequestMapping("/showPage")
    public String loadZkCluster(Model model) {
        model.addAttribute("list", zkClusterService.getZKClusters());
        return "/zkcluster/add";
    }

    @RequestMapping(value = "/addZkCluster", method = RequestMethod.POST)
    @JsonBody
    public Object addZkCluster(@RequestParam("name") String name, @RequestParam("address") String address) {
        logger.info("add zk cluster, name={}, address={}", name, address);

        name = name.trim();
        address = address.trim();
        if (name.length() == 0 || address.length() == 0) {
            return errorJson("机房名或者机房地址为空！");
        }

        if (name.length() > CLUSTER_NAME_MAX_SIZE) {
            return errorJson("机房名长度不能大于" + CLUSTER_NAME_MAX_SIZE);
        } else if (address.length() > CLUSTER_ADDRESS_MAX_SIZE) {
            return errorJson("机房地址长度不能大于" + CLUSTER_ADDRESS_MAX_SIZE);
        }

        ZKCluster zkCluster = new ZKCluster(name, address);
        try {
            if (!zkClusterService.addNewZK(zkCluster)) {
                logger.warn("zk cluster is existed, {}", zkCluster);
                return errorJson("该地址已存在！");
            }
        } catch (Throwable e) {
            logger.error("add zk cluster error, {}", zkCluster, e);
            return errorJson("添加失败");
        }
        return true;
    }
}
