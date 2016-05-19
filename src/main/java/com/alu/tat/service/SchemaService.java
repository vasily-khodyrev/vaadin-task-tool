package com.alu.tat.service;

import com.alu.tat.entity.dao.BaseDao;
import com.alu.tat.entity.schema.Schema;

import java.util.Collection;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/8/2015
 */
public class SchemaService {
    private static SchemaService instance = new SchemaService();

    public static SchemaService getInstance() {
        return instance;
    }

    private SchemaService() {
    }

    public static Collection<Schema> getSchemas() {
        return BaseDao.getAll(Schema.class);
    }

    public static Schema getSchema(Long id) {
        return BaseDao.getById(id, Schema.class);
    }

    public static void addSchema(Schema t) {
        BaseDao.create(t);
    }

    public static void updateSchema(Schema t) {
        BaseDao.update(t);
    }

    public static void removeSchema(Long id) {
        BaseDao.removeById(id, Schema.class);
    }
}
