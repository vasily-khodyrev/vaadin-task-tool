package com.alu.tat.init;

import com.alu.tat.entity.Folder;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.User;
import com.alu.tat.entity.dao.BaseDao;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.FolderService;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.alu.tat.service.UserService;
import com.alu.tat.util.HibernateUtil;
import com.alu.tat.util.PasswordTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Collection;
import java.util.List;

/**
 * Created by imalolet on 6/19/2015.
 */
public class Init extends HttpServlet {

    private final static Logger logger =
            LoggerFactory.getLogger(Init.class);

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            logger.debug("Preparing initial DB data...");
            initData();
            logger.debug("Checking if migration needed...");
            migrateDataIfNeeded();
        } catch (Exception e) {
            logger.error("error while initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            logger.debug("Shutting down Hibernate...");
            HibernateUtil.shutdown();
        } catch (Exception e) {
            logger.error("error while shutting down hibernate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initData() {
        Collection<User> allUsers = UserService.getUsers();
        User admin = new User();
        admin.setLogin("admin");
        admin.setName("Admin");
        admin.setPasswordHash(PasswordTools.getPwdHash("admin"));
        admin.setIsSystem(true);
        if (!allUsers.contains(admin)) {
            logger.debug("Creating default admin...");
            UserService.createUser(admin);
        }
        if (allUsers.isEmpty()) {
            logger.debug("Creating users set...");
            User imalolet = createUser("imalolet", "imalolet", "Igor Maloletniy");
            createUser("ibotian", "ibotian", "Igor Botian");
            createUser("mivanova", "mivanova", "Maya Ivanova");
            createUser("kkharlin", "kkharlin", "Konstantin Kharlin");
            createUser("jkubasov", "jkubasov", "Julia Vasilieva");
            createUser("mdmitrak", "mdmitrak", "Mikhail Dmitrakh");
            createUser("alexeyan", "alexeyan", "Alexey Antonov");
            createUser("valeryp", "valeryp", "Valery Pavlov");
            createUser("vkhodyre", "vkhodyre", "Vasily Khodyrev");

            logger.debug("Creating default schemas...");
            Schema defaultSchema = new Schema();
            defaultSchema.setIsSystem(true);
            defaultSchema.setIsdefault(true);
            defaultSchema.setName("Detailed Analysis");
            defaultSchema.setDescription("Detailed analysis schema");
            List<SchemaElement> list = defaultSchema.getElementsList();
            list.add(new SchemaElement("General", "General aspects", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("SDD", "FSD/FDD/SDD/Testplan needed?", SchemaElement.ElemType.BOOLEAN, 8));
            list.add(new SchemaElement("Applicable OT Solution", "Define the solution applicable (OTMS/OTBE/OTMC/...)", SchemaElement.ElemType.MULTI_ENUM, "OTMS;OTBE;OTMC", 0));

            list.add(new SchemaElement("Models", "All model description related aspects", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("New models", "Do we need to create new models?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("Model changes", "Do we need to change existing models?", SchemaElement.ElemType.BOOLEAN, 2));
            list.add(new SchemaElement("cmsUser/MyProfile/quickUser affected", "Does pseudo models affected?", SchemaElement.ElemType.BOOLEAN, 0));
            list.add(new SchemaElement("Models generation affected?", "Do we need to change model generation part?", SchemaElement.ElemType.BOOLEAN, 8));

            list.add(new SchemaElement("Translations", "All localization aspects", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("Models localization", "Do we need to add/change model change localization?", SchemaElement.ElemType.BOOLEAN, 2));
            list.add(new SchemaElement("Exceptions localization", "Do we need to add/change exception localization?", SchemaElement.ElemType.BOOLEAN, 2));

            list.add(new SchemaElement("Business logic", "All business logic aspects", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("Enhancer / CoherenceChecker / Post-pre actions", "Does the new Business logic affects pointed items?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("New/changed alarms", "Do we need to create/update alarms?", SchemaElement.ElemType.BOOLEAN, 2));
            list.add(new SchemaElement("Describe cases", "Describe each business case that must be implemented", SchemaElement.ElemType.MULTI_STRING, 4));

            list.add(new SchemaElement("EasyAdmin / Migration / Audit", "All easy admin/migration/audit aspects", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("EasyAdmin", "Do we need create/update easy admin?", SchemaElement.ElemType.BOOLEAN, 2));
            list.add(new SchemaElement("ICE2ICE migration needed", "Do we need migration from previous OT version?", SchemaElement.ElemType.BOOLEAN, 0));
            list.add(new SchemaElement("*Liquibase migration needed", "Do we need to migration DB schema or data?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("*Broker migration needed", "Do we need to migration something during Broker migration phase?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("*Post ready migration", "Do we need POST-READY migration(for external systems like ACS)?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("Audit", "Do we need to change/implement some audit procedures?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("ICS2ICE migration needed", "Does it impacts migration from ICS6.x?", SchemaElement.ElemType.BOOLEAN, 8));


            list.add(new SchemaElement("Testing", "All the testing aspects", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("Unit tests needed", "Do we need unit tests?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("Functional tests needed", "Do we need functional tests?", SchemaElement.ElemType.BOOLEAN, 8));
            list.add(new SchemaElement("Integration tests needed", "Do we need integration tests?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("ICE2ICE migration tests needed", "Do we need migration from previous version tests?", SchemaElement.ElemType.BOOLEAN, 8));
            list.add(new SchemaElement("ICS2ICE migration tests needed", "Do we need migration from ICS6.x tests?", SchemaElement.ElemType.BOOLEAN, 8));
            list.add(new SchemaElement("Manual tests needed", "Do we need manual testing?", SchemaElement.ElemType.BOOLEAN, 4));
            list.add(new SchemaElement("Auto GUI WBM/MyProfile tests needed", "Do we need auto GUI tests?", SchemaElement.ElemType.BOOLEAN, 8));


            list.add(new SchemaElement("Environment", "All the environment aspects", SchemaElement.ElemType.DOMAIN, 0));
            list.add(new SchemaElement("Should platforms be prepared?", "Do we need to prepare some environment before dev/test?", SchemaElement.ElemType.BOOLEAN, 8));
            BaseDao.create(defaultSchema);

            Schema secondSchema = new Schema();
            secondSchema.setIsSystem(true);
            secondSchema.setName("High Level Analysis");
            secondSchema.setDescription("High Level Analysis Schema");
            List<SchemaElement> secondList = secondSchema.getElementsList();
            secondList.add(new SchemaElement("General", "General", SchemaElement.ElemType.DOMAIN, 0));
            secondList.add(new SchemaElement("FSD", "Do we need to provide contribution for the FSD?", SchemaElement.ElemType.BOOLEAN, 5));
            secondList.add(new SchemaElement("FDD", "Do we need to create/update FDD?", SchemaElement.ElemType.BOOLEAN, 5));
            secondList.add(new SchemaElement("SDD", "Do we need a dedicated component SDD or update internal docs?", SchemaElement.ElemType.BOOLEAN, 5));
            secondList.add(new SchemaElement("Applicable otSolution", "Define the solution applicable (OTMS/OTBE/OTMC/...)", SchemaElement.ElemType.MULTI_ENUM, "OTMS;OTBE;OTMC", 0));
            secondList.add(new SchemaElement("Cases", "Cases", SchemaElement.ElemType.DOMAIN, 0));
            secondList.add(new SchemaElement("Cases", "Describe your case here.", SchemaElement.ElemType.MULTI_STRING, 5));
            BaseDao.create(secondSchema);

            logger.debug("Creating default folders...");
            Folder f1 = new Folder();
            f1.setName("OT10");
            Folder f2 = new Folder();
            f2.setName("OT11");
            FolderService.createFolder(f1);
            FolderService.createFolder(f2);

            for (int i = 0; i < 5; i++) {
                Task t = new Task();
                t.setId(System.currentTimeMillis());
                t.setAuthor(imalolet);
                t.setDescription("description of crqms" + i);
                t.setName("crqms" + i);
                t.setSchema(defaultSchema);
                t.setFolder(((int) (Math.round(Math.random()))) % 2 == 1 ? f1 : f2);

                BaseDao.create(t);
            }
        }

    }

    private static User createUser(String login, String pwd, String fullName) {
        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(PasswordTools.getPwdHash(pwd));
        user.setName(fullName);
        UserService.createUser(user);
        return user;
    }

    private void migrateDataIfNeeded() {
        migrateSchemas();
        logger.debug("Migrating task...");
        if (migrateTasks()) {
            logger.debug("Migration of task completed.");
        } else {
            logger.debug("Migration of task not needed.");
        }
    }

    private void migrateSchemas() {
        Collection<Schema> schemas = SchemaService.getNotDeprecatedSchemas();
        if (schemas.isEmpty()) {
            Collection<Schema> allSchemas = SchemaService.getSchemas();
            for (Schema schema : allSchemas) {
                schema.setDeprecated(false);
                SchemaService.updateSchema(schema);
            }
        }
    }

    private boolean migrateTasks() {
        boolean res = false;
        List<Task> tasks = TaskService.findTasksWOStatus();
        if (tasks != null) {
            for (Task t : tasks) {
                res = true;
                logger.debug("Migrating task " + t.getName() + "...");
                t.setStatus(Task.Status.NEW);
                TaskService.updateTask(t);
            }
        }
        return res;
    }


}
