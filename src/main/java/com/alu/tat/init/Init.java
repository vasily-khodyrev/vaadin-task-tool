package com.alu.tat.init;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.User;
import com.alu.tat.entity.dao.BaseDao;

/**
 * Created by imalolet on 6/19/2015.
 */
public class Init extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        initData();
    }

    private void initData() {
        User user = new User();
        user.setName("Igor Maloletniy");
        BaseDao.create(user);

        for (int i = 0; i < 20; i++) {
            Task t = new Task();

            t.setId(System.currentTimeMillis());
            t.setAuthor(user);
            t.setDescription("description of crqms" + i);
            t.setName("crqms" + i);
            final Task.Release release = Task.Release.values()[((int) (Math.round(Math.random())))];
            t.setRelease(release);

            BaseDao.create(t);
        }
    }
}
