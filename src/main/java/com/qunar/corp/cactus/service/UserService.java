package com.qunar.corp.cactus.service;

import com.qunar.corp.cactus.bean.User;

import java.util.Map;

/**
 * Date: 13-11-7 Time: 下午3:41
 * 
 * @author: xiao.liang
 * @description:
 */
public interface UserService {

    int add(User user);

    User loadUser(String username);
}
