package com.alu.tat.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alu.tat.entity.Folder;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.User;
import com.alu.tat.entity.dao.BaseDao;
import com.alu.tat.entity.schema.Schema;

/**
 * Created by imalolet on 6/11/2015.
 */
public class TaskService {

    public static Collection<Task> getTasks() {
        return BaseDao.getAll(Task.class);
    }

    public static Task getTask(Long id) {
        return BaseDao.getById(id, Task.class);
    }

    public static void addTask(Task t) {
        BaseDao.create(t);
    }

    public static void updateTask(Task t) {
        BaseDao.update(t);
    }

    public static void removeTask(Long id) {
        BaseDao.removeById(id, Task.class);
    }

    public static List<Task> findTaskByFolder(Folder release) {
        Map<String, Object> params = new HashMap<>();
        params.put("folder", release);
        return BaseDao.find(Task.class, "findTaskByFolder", params);
    }

    public static List<Task> findTaskBySchema(Schema schema) {
        Map<String, Object> params = new HashMap<>();
        params.put("schema", schema);
        return BaseDao.find(Task.class, "findTaskBySchema", params);
    }

    public static List<Task> findTaskByUser(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        return BaseDao.find(Task.class, "findTaskByUser", params);
    }

    public static List<Task> findTasksWOStatus() {
        Map<String, Object> params = new HashMap<>();
        return BaseDao.find(Task.class, "findTasksWOStatus", params);
    }
}
