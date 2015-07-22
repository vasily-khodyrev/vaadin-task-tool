package com.alu.tat;

import com.alu.tat.view.*;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Created by imalolet on 6/10/2015.
 */
@Title("Task tool")
@Theme("valo")
public class Main extends UI {
    static {
        SLF4JBridgeHandler.install();
    }
    private final static Logger logger =
            LoggerFactory.getLogger(Main.class);

    private Navigator navigator;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        navigator = new Navigator(this, this);
        navigator.addView(UIConstants.VIEW_MAIN, MainView.class);
        navigator.addView(UIConstants.VIEW_TASK, TaskView.class);
        navigator.addView(UIConstants.VIEW_SCHEMA, SchemaView.class);
        navigator.setErrorView(ErrorView.class);
        logger.warn("Main UI navigation initialized");
    }
}
