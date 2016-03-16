package com.alu.tat.init;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.User;
import com.alu.tat.entity.dao.BaseDao;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.UserService;
import com.alu.tat.util.HibernateUtil;
import com.alu.tat.util.PasswordTools;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.List;

/**
 * Created by imalolet on 6/19/2015.
 */
public class Init extends HttpServlet {

    //private final static Logger logger =
    //        LoggerFactory.getLogger(Init.class);

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            initData();
        } catch (Exception e) {
            System.err.println("error while initializing data: " +  e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            HibernateUtil.shutdown();
        } catch (Exception e) {
            System.err.println("error while shutting down hibernate: " +  e.getMessage());
            e.printStackTrace();
        }
    }

    private void initData() {
        List<User> allUsers = BaseDao.getAll(User.class);
        if (allUsers.isEmpty()) {
            User user = new User();
            user.setLogin("imalolet");
            user.setPasswordHash(PasswordTools.getPwdHash("imalolet"));
            user.setName("Igor Maloletniy");
            UserService.createUser(user);

            Schema defaultSchema = new Schema();
            defaultSchema.setName("Default schema");
            defaultSchema.setDescription("Default schema");
            List<SchemaElement> list = defaultSchema.getElementsList();
            list.add(new SchemaElement("General", "Do we need SDD?", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("SDD", "Do we need SDD?", SchemaElement.ElemType.BOOLEAN, 5));
            list.add(new SchemaElement("impl", "Do we need to do it?", SchemaElement.ElemType.BOOLEAN, 5));
            list.add(new SchemaElement("tests", "How many tests?", SchemaElement.ElemType.BOOLEAN, 5));
            list.add(new SchemaElement("Details", "Do we need SDD?", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("Question1?", "Do we need SDD?", SchemaElement.ElemType.BOOLEAN, 5));
            list.add(new SchemaElement("Question2?", "Do we need to do it?", SchemaElement.ElemType.BOOLEAN, 5));
            list.add(new SchemaElement("Question3?", "How many tests?", SchemaElement.ElemType.INTEGER, 5));
            BaseDao.create(defaultSchema);

            Schema secondSchema = new Schema();
            secondSchema.setName("Second schema");
            secondSchema.setDescription("Second schema");
            List<SchemaElement> secondList = secondSchema.getElementsList();
            secondList.add(new SchemaElement("General", "Do we need SDD?", SchemaElement.ElemType.DOMAIN, 0));
            secondList.add(new SchemaElement("SDD", "Do we need SDD?", SchemaElement.ElemType.BOOLEAN, 5));
            BaseDao.create(secondSchema);

            for (int i = 0; i < 20; i++) {
                Task t = new Task();

                t.setId(System.currentTimeMillis());
                t.setAuthor(user);
                t.setDescription("description of crqms" + i);
                t.setName("crqms" + i);
                t.setSchema(defaultSchema);
                final Task.Release release = Task.Release.values()[((int) (Math.round(Math.random())))];
                t.setRelease(release);

                BaseDao.create(t);
            }
        }

    }
}
