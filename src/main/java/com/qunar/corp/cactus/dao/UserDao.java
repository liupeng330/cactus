package com.qunar.corp.cactus.dao;

import com.qunar.corp.cactus.bean.User;

import java.util.List;
import java.util.Map;

/**
 * Date: 13-11-7 Time: 下午3:27
 * 
 * @author: xiao.liang
 * @description:
 */
public interface UserDao {

    int add(User user);

    List<User> load(Map<String, Object> param);
}
