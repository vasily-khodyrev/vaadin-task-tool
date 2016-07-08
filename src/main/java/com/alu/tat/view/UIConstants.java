package com.alu.tat.view;

import com.vaadin.server.ThemeResource;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 13.07.15
 */
public interface UIConstants {
    //Resources
    public static final ThemeResource FOLDER_ICON = new ThemeResource("../runo/icons/16/folder.png");

    //Views
    public final static String VIEW_REPORT = "Report";
    public final static String VIEW_REPORTLIST = "ReportList";
    public final static String VIEW_TASK = "Task";
    public final static String VIEW_SCHEMA = "Schema";
    public final static String VIEW_USER = "User";
    public final static String VIEW_FOLDER = "Folder";
    public final static String VIEW_MAIN = "";
    public final static String VIEW_LOGIN = "Login";
    public final static String VIEW_ERROR = "Error";

    //Operations
    public final static String OP_CREATE = "Create";
    public final static String OP_UPDATE = "Update";
    public final static String OP_SHOW = "Show";


    //Actions
    public final static String TASK_CREATE = VIEW_TASK + "/" + OP_CREATE;
    public final static String TASK_UPDATE = VIEW_TASK + "/" + OP_UPDATE + "/";
    public final static String USER_CREATE = VIEW_USER + "/" + OP_CREATE;
    public final static String USER_UPDATE = VIEW_USER + "/" + OP_UPDATE + "/";
    public final static String FOLDER_CREATE = VIEW_FOLDER + "/" + OP_CREATE;
    public final static String FOLDER_UPDATE = VIEW_FOLDER + "/" + OP_UPDATE + "/";
    public final static String SCHEMA_CREATE = VIEW_SCHEMA + "/" + OP_CREATE;
    public final static String SCHEMA_UPDATE = VIEW_SCHEMA + "/" + OP_UPDATE + "/";
    public final static String REPORT_SHOW = VIEW_REPORT + "/" + OP_SHOW + "/";

}
