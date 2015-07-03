package com.alu.tat.service;

import java.util.Collection;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.dao.BaseDao;

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
        BaseDao.removeById(id,Task.class);
    }
}
