package com.alu.tat;

import com.alu.tat.view.MainView;
import com.alu.tat.view.TaskView;
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

    public static String CREATE_VIEW = "TASK";
    public static String MAIN_VIEW = "";
    public static String LOGIN_VIEW = "LOGIN";

    private Navigator navigator;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        navigator = new Navigator(this, this);
        navigator.addView(MAIN_VIEW, MainView.class);
        navigator.addView(CREATE_VIEW, TaskView.class);

    }


}
