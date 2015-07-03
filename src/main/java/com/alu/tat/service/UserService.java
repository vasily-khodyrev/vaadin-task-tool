package com.alu.tat.service;

import com.alu.tat.entity.User;
import com.alu.tat.entity.dao.BaseDao;

/**
 * Created by imalolet on 6/19/2015.
 */
public class UserService {

    public static void createUser(User u) {
        BaseDao.create(u);
    }

    public static User currentUser(){
        return BaseDao.getAll(User.class).get(0);
    }
}
