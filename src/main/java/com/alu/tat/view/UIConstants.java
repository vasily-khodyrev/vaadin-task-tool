package com.alu.tat.view;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 13.07.15
 */
public interface UIConstants {

    //Views
    public final static String VIEW_TASK = "Task";
    public final static String VIEW_SCHEMA = "Schema";
    public final static String VIEW_MAIN = "";
    public final static String VIEW_LOGIN = "Login";
    public final static String VIEW_ERROR = "Error";

    //Operations
    public final static String OP_CREATE = "Create";
    public final static String OP_UPDATE = "Update";


    //Actions
    public final static String TASK_CREATE = VIEW_TASK + "/" + OP_CREATE;
    public final static String TASK_UPDATE = VIEW_TASK + "/" + OP_UPDATE+"/";
    public final static String SCHEMA_CREATE = VIEW_SCHEMA + "/" + OP_CREATE;
    public final static String SCHEMA_UPDATE = VIEW_SCHEMA + "/" + OP_UPDATE+"/";

}
