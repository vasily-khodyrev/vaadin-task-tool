package com.alu.tat.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.dao.BaseDao;
import com.alu.tat.entity.schema.Schema;

/**
 * Created by imalolet on 6/11/2015.
 */
public class TaskService {

    private static TaskService instance = new TaskService();

    public static TaskService getInstance() {
        return instance;
    }

    private TaskService() {
    }

    public Collection<Task> getTasks() {
        return BaseDao.getAll(Task.class);
    }

    public Task getTask(Long id) {
        return BaseDao.getById(id, Task.class);
    }

    public void addTask(Task t) {
        BaseDao.create(t);
    }

    public void updateTask(Task t) {
        BaseDao.update(t);
    }

    public void removeTask(Long id) {
        BaseDao.removeById(id, Task.class);
    }

    public List<Task> findTaskByRelease(Task.Release release) {
        Map<String, Object> params = new HashMap<>();
        params.put("release", release);
        return BaseDao.find(Task.class, "findTaskByRelease", params);
    }

    public List<Task> findTaskBySchema(Schema schema) {
        Map<String, Object> params = new HashMap<>();
        params.put("schema", schema);
        return BaseDao.find(Task.class, "findTaskBySchema", params);
    }
}
