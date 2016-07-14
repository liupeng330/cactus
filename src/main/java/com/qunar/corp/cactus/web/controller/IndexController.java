package com.qunar.corp.cactus.web.controller;

import com.qunar.corp.cactus.service.graph.GraphService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: xiao.liang
 * Date: 13-10-24
 * Time: 下午5:23
 */
@Controller
public class IndexController extends AbstractController {

    @Resource
    private GraphService graphService;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("statCount", graphService.getStats());
        return "index";
    }
}
