package com.alu.tat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 18.06.2016
 */
public class HttpSessionCollector implements HttpSessionListener {
    private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

    private final static Logger logger =
            LoggerFactory.getLogger(HttpSessionCollector.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.put(session.getId(), session);
        logger.debug("Session " + session.getId() + " created.");
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.remove(session.getId());
        logger.debug("Session " + session.getId() + " destroyed.");
    }

    public static HttpSession find(String sessionId) {
        return sessions.get(sessionId);
    }

    public static Map<String, HttpSession> getSessions() {
        return sessions;
    }
}
