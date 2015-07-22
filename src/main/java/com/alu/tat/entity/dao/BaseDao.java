package com.alu.tat.entity.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.alu.tat.entity.BaseEntity;
import com.alu.tat.util.HibernateUtil;

/**
 * Created by imalolet on 6/19/2015.
 */
public class BaseDao {
    public static void create(BaseEntity entity) {
        final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        final Transaction transaction = session.beginTransaction();

        final Long id = (Long) session.save(entity);

        transaction.commit();
        entity.setId(id);
    }

    public static void update(BaseEntity entity) {
        final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        final Transaction transaction = session.beginTransaction();

        session.update(entity);
        transaction.commit();
    }

    public static <T> List<T> getAll(Class<T> clazz) {
        final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        final Transaction transaction = session.beginTransaction();
        final List result = session.createCriteria(clazz).list();
        transaction.commit();

        return result;
    }

    public static <T> T getById(Long id, Class<T> clazz) {
        final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        final Transaction transaction = session.beginTransaction();
        final T result = (T) session.get(clazz, id);
        transaction.commit();
        return result;

    }

    public static <T> void removeById(Long id, Class<T> clazz) {
        final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        final Transaction transaction = session.beginTransaction();
        final T item = (T) session.get(clazz,id);
        session.delete(item);
        transaction.commit();
    }

    public static <T> List<T> find(Class<T> clazz, String query, Map params) {
        final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        final Transaction transaction = session.beginTransaction();
        Query q = session.getNamedQuery(query);
        q.setProperties(params);
        final List<T> result = q.list();
        transaction.commit();
        return result;
    }
}
