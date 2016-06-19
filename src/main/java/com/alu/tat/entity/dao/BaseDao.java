package com.alu.tat.entity.dao;

import com.alu.tat.entity.BaseEntity;
import com.alu.tat.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Map;

/**
 * Created by imalolet on 6/19/2015.
 */
public class BaseDao {
    private final static Logger logger = LoggerFactory.getLogger(BaseDao.class);

    public static void create(BaseEntity entity) {
        final Session session = getSession();
        final Transaction transaction = session.getTransaction();
        Long id = 0L;
        try {
            transaction.begin();
            id = (Long) session.save(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            logger.error("Error while creating entity " + entity, e);
            transaction.rollback();
            throw e;
        }
        entity.setId(id);
    }

    public static void update(BaseEntity entity) {
        final Session session = getSession();
        final Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            session.update(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            logger.error("Error while updating entity " + entity, e);
            transaction.rollback();
            throw e;
        }
    }

    public static <T> List<T> getAll(Class<T> clazz) {
        final Session session = getSession();
        final Transaction transaction = session.getTransaction();
        List result = null;
        try {
            transaction.begin();
            result = session.createCriteria(clazz).list();
            transaction.commit();
        } catch (RuntimeException e) {
            logger.error("Error while getting all entities Class:" + clazz.getSimpleName(), e);
            transaction.rollback();
            throw e;
        }
        return result;
    }

    public static <T> T getById(Long id, Class<T> clazz) {
        final Session session = getSession();
        final Transaction transaction = session.getTransaction();
        T result = null;
        try {
            transaction.begin();
            result = (T) session.get(clazz, id);
            transaction.commit();
        } catch (RuntimeException e) {
            logger.error("Error while getting Class:" + clazz.getSimpleName() + " id=" + id, e);
            transaction.rollback();
            throw e;
        }
        return result;

    }

    public static <T> void removeById(Long id, Class<T> clazz) {
        final Session session = getSession();
        final Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            final T item = (T) session.get(clazz, id);
            checkItem(item);
            session.delete(item);
            transaction.commit();
        } catch (RuntimeException e) {
            logger.error("Error while removing entity Class:" + clazz.getSimpleName() + " id=" + id, e);
            transaction.rollback();
            throw e;
        }
    }

    public static <T> List<T> find(Class<T> clazz, String query, Map params) {
        final Session session = getSession();
        final Transaction transaction = session.getTransaction();
        List<T> result = null;
        try {
            transaction.begin();
            Query q = session.getNamedQuery(query);
            q.setProperties(params);
            result = q.list();
            transaction.commit();
        } catch (RuntimeException e) {
            logger.error("Error while searching entities " + clazz.getSimpleName() + " query=" + query, e);
            transaction.rollback();
            throw e;
        }
        return result;
    }

    private static <T> void checkItem(T item) {
        if (item instanceof BaseEntity) {
            BaseEntity be = (BaseEntity) item;
            if (be.getIsSystem() != null && be.getIsSystem()) {
                throw new PersistenceException("It's prohibited to remove system objects!");
            }
        }
    }

    private static Session getSession() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.getCurrentSession();
        if (!session.isOpen()) {
            session.close();
            session = sf.openSession();
        }
        return session;
    }
}
