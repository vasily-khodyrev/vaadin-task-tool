package com.alu.tat.service;

import com.alu.tat.entity.User;
import com.alu.tat.entity.dao.BaseDao;
import com.alu.tat.entity.schema.Schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/8/2015
 */
public class SchemaService {
    public static Collection<Schema> getSchemas() {
        return BaseDao.getAll(Schema.class);
    }

    public static Collection<Schema> getNotDeprecatedSchemas() {
        Map<String, Object> params = new HashMap<>();
        params.put("deprecated", false);
        List<Schema> schemas = BaseDao.find(Schema.class, "findNotDeprecatedSchemas", params);
        return schemas;
    }

    public static Schema getDefaultSchema() {
        Map<String, Object> params = new HashMap<>();
        params.put("isdefault", true);
        List<Schema> schemas = BaseDao.find(Schema.class, "getDefaultSchemas", params);
        if (schemas.size() > 0) {
            return schemas.iterator().next();
        } else {
            return null;
        }
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
