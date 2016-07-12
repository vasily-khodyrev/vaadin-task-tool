package com.alu.tat;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/17/2016
 */
public class TaskAnalysisServlet extends VaadinServlet
        implements SessionInitListener {

    private final static Logger logger =
            LoggerFactory.getLogger(TaskAnalysisServlet.class);

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event)
            throws ServiceException {
        //set session timeout to 30 min
        int timeout = 60 * 60;
        logger.debug("New session established. Setting session timeout to " + timeout + "sec");
        event.getSession().getSession().setMaxInactiveInterval(timeout);
    }
}
