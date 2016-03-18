package com.alu.tat;

import com.vaadin.server.*;

import javax.servlet.ServletException;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/17/2016
 */
public class TaskAnalysisServlet extends VaadinServlet
        implements SessionInitListener {

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event)
            throws ServiceException {
        //set session timeout to 30 min
        event.getSession().getSession().setMaxInactiveInterval(30*60);
    }
}
