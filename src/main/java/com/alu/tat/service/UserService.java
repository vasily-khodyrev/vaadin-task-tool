package com.alu.tat.service;

import com.alu.tat.entity.User;
import com.alu.tat.entity.dao.BaseDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by imalolet on 6/19/2015.
 */
public class UserService {

    public static void createUser(User u) {
        BaseDao.create(u);
    }

    public static User getUserById(Long id) {
        return BaseDao.getById(id, User.class);
    }

    public static User getUser(String login) {
        Map<String, Object> params = new HashMap<>();
        params.put("login", login);
        List<User> users = BaseDao.find(User.class, "findUserByLogin", params);
        if (users.size() == 1) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public static Collection<User> getUsers() {
        return BaseDao.getAll(User.class);
    }

    public static void updateUser(User user) {
        BaseDao.update(user);
    }

    public static void removeUser(Long id) {
        BaseDao.removeById(id, User.class);
    }
}
