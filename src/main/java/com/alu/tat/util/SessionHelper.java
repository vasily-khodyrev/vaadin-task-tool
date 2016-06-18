package com.alu.tat.util;

import com.alu.tat.entity.User;
import com.vaadin.server.VaadinSession;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 18.06.2016
 */
public class SessionHelper {

    public static final String USER = "user";

    public static void setCurrentUser(VaadinSession session, User user){
         session.setAttribute(USER, user);
    }

    public static User getCurrentUser(VaadinSession session){
        return (User) session.getAttribute(USER);
    }
}
