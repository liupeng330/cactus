package com.qunar.corp.cactus.dao.impl;

import com.qunar.corp.cactus.bean.User;
import com.qunar.corp.cactus.dao.UserDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Date: 13-11-7
 * Time: 下午3:29
 *
 * @author: xiao.liang
 * @description:
 */
@Repository
public class UserDaoImpl extends AbstractDao implements UserDao {

    @Override
    public int add(User user) {
        return sqlSession.insert("user.insert",user);
    }

    @Override
    public List<User> load(Map<String,Object> param) {
       return sqlSession.selectList("user.select",param);
    }
}
