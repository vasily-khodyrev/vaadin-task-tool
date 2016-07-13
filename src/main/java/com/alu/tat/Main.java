package com.alu.tat;

import com.alu.tat.entity.User;
import com.alu.tat.util.SessionHelper;
import com.alu.tat.view.*;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.UIEvents;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Created by imalolet on 6/10/2015.
 */
@Title("Task tool")
@Theme("tasktool")
@PreserveOnRefresh
public class Main extends UI {
    static {
        SLF4JBridgeHandler.install();
        initLog4j();
    }

    private final static Logger logger =
            LoggerFactory.getLogger(Main.class);

    private Navigator navigator;

    private static void initLog4j() {
        PropertyConfigurator.configureAndWatch("log4j.properties");
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final int pollint = 5 * 60 * 1000;
        logger.debug("Setting poll interval to " + pollint / 1000 + " sec");
        setPollInterval(pollint);

        addPollListener(new UIEvents.PollListener() {
            int counter = 0;

            @Override
            public void poll(UIEvents.PollEvent event) {
                String loc = event.getUI().getPage().getLocation().toString();
                WebBrowser browser = event.getUI().getPage().getWebBrowser();
                String broserApp = browser.getBrowserApplication();
                String address = browser.getAddress();
                String view = event.getUI().getNavigator().getCurrentView().getClass().getName();
                User u = SessionHelper.getCurrentUser(event.getUI().getSession());
                String user = u != null ? u.getLogin() : "notLoggedIn";
                logger.debug("POLL " + pollint / 1000 + " sec : " + counter++ + " user = " + user + " " + loc + " " + view + " Browser: " + broserApp + " Addr: " + address);
            }
        });

        navigator = new Navigator(this, this);
        navigator.addView(UIConstants.VIEW_MAIN, MainView.class);
        navigator.addView(UIConstants.VIEW_TASK, TaskView.class);
        navigator.addView(UIConstants.VIEW_SCHEMA, SchemaView.class);
        navigator.addView(UIConstants.VIEW_LOGIN, LoginView.class);
        navigator.addView(UIConstants.VIEW_USER, UserView.class);
        navigator.addView(UIConstants.VIEW_FOLDER, FolderView.class);
        navigator.addView(UIConstants.VIEW_REPORT, ReportView.class);
        navigator.addView(UIConstants.VIEW_REPORTLIST, ReportListView.class);
        navigator.setErrorView(ErrorView.class);
        //
        // We use a view change handler to ensure the user is always redirected
        // to the login view if the user is not logged in.
        //
        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {

                // Check if a user has logged in
                boolean isLoggedIn = getSession().getAttribute("user") != null;
                boolean isLoginView = event.getNewView() instanceof LoginView;
                boolean isReportView = event.getNewView() instanceof ReportView;
                boolean isReportListView = event.getNewView() instanceof ReportListView;

                if (!isLoggedIn && !isLoginView) {
                    // Redirect to login view always if a user has not yet
                    // logged in
                    if (isReportView || isReportListView) {
                        return true;
                    }
                    getNavigator().navigateTo(UIConstants.VIEW_LOGIN);
                    return false;

                } else if (isLoggedIn && isLoginView) {
                    // If someone tries to access to login view while logged in,
                    // then cancel
                    //getNavigator().navigateTo(UIConstants.VIEW_MAIN);
                    return false;
                }

                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

            }
        });
        logger.warn("Main UI navigation initialized");
    }
}
