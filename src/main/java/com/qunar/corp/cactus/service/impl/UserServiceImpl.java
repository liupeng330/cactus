package com.qunar.corp.cactus.service.impl;

import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.User;
import com.qunar.corp.cactus.dao.UserDao;
import com.qunar.corp.cactus.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Date: 13-11-7 Time: 下午3:43
 * 
 * @author: xiao.liang
 * @description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public int add(User user) {
        return userDao.add(user);
    }

    @Override
    public User loadUser(String username) {
        Map<String, Object> param = Maps.newHashMapWithExpectedSize(1);
        param.put("username", username);
        return loadOne(param);
    }

    private User loadOne(Map<String, Object> param) {
        List<User> list = userDao.load(param);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
