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

    public Collection<Schema> getSchemas() {
        return BaseDao.getAll(Schema.class);
    }

    public Schema getSchema(Long id) {
        return BaseDao.getById(id, Schema.class);
    }

    public void addSchema(Schema t) {
        BaseDao.create(t);
    }

    public void updateSchema(Schema t) {
        BaseDao.update(t);
    }

    public void removeSchema(Long id) {
        BaseDao.removeById(id, Schema.class);
    }
}
