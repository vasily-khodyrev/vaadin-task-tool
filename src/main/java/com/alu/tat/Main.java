package com.alu.tat;

import com.alu.tat.view.*;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * Created by imalolet on 6/10/2015.
 */
@Title("Task tool")
@Theme("valo")
public class Main extends UI {

    private Navigator navigator;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        navigator = new Navigator(this, this);
        navigator.addView(UIConstants.VIEW_MAIN, MainView.class);
        navigator.addView(UIConstants.VIEW_TASK, TaskView.class);
        navigator.addView(UIConstants.VIEW_SCHEMA, SchemaView.class);
        navigator.setErrorView(ErrorView.class);
    }
}
