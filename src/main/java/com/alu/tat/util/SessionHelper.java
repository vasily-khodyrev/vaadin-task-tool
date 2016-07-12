package com.alu.tat.util;

import com.alu.tat.entity.User;
import com.alu.tat.view.LoginView;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 18.06.2016
 */
public class SessionHelper {
    private final static Logger logger =
            LoggerFactory.getLogger(SessionHelper.class);

    public static final String USER = "user";

    public static void setCurrentUser(VaadinSession session, User user) {
        session.setAttribute(USER, user);
    }

    public static User getCurrentUser(VaadinSession session) {
        return (User) session.getAttribute(USER);
    }

    public static void notifyAllUsers(final String msg) {
        List<VaadinSession> vSessions = new LinkedList<>();
        for (HttpSession httpSession : HttpSessionCollector.getSessions().values()) {
            vSessions.addAll(VaadinSession.getAllSessions(httpSession));
        }

        for (VaadinSession vSession : vSessions) {
            for (final UI ui : vSession.getUIs()) {
                final Page page = ui.getPage();
                String view = ui.getNavigator().getCurrentView().getClass().getName();
                boolean isLoginView = ui.getNavigator().getCurrentView() instanceof LoginView;
                User u = getCurrentUser(ui.getSession());
                String user = u != null ? u.getLogin() : "Anonymous";
                if (!isLoginView) {
                    logger.debug("Notify user '" + user + "' on page '" + view + "' with msg '" + msg + "'.");
                    final Notification notification = new Notification("Dear user '" + user + "' FYI : " + msg, Notification.Type.WARNING_MESSAGE);
                    ui.access(new Runnable() {
                        @Override
                        public void run() {
                            notification.show(page);
                        }
                    });
                }
            }
        }
    }
}
