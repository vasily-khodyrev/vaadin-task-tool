package com.alu.tat.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by imalolet on 6/19/2015.
 */

public class HibernateUtil {
    private final static Logger logger =
            LoggerFactory.getLogger(HibernateUtil.class);
    private static volatile SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            logger.debug("Hibernate initialized.");
            return sessionFactory;

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            logger.error("Initial SessionFactory creation failed.",ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
           sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }

}