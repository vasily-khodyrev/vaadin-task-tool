package com.alu.tat.view;

import com.alu.tat.entity.Task;
import com.alu.tat.service.TaskService;
import com.alu.tat.util.TaskPresenter;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 04.07.2016
 */
public class ReportView extends AbstractActionView {
    private final static Logger logger =
            LoggerFactory.getLogger(ReportView.class);

    private Navigator navigator;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final Long showId = getShowId(event.getParameters());
        navigator = getUI().getNavigator();
        Task task = null;
        try {
            if (showId != null) {
                task = TaskService.getTask(showId);
            }
        } catch (Exception e) {
            logger.error("Task with id " + showId + " not found");
        }
        if (task != null) {
            Panel infoPanel = new Panel();
            infoPanel.setSizeFull();
            String s = TaskPresenter.getHtmlView(task);
            RichTextArea text = new RichTextArea();
            text.setValue(s);
            text.setReadOnly(true);
            text.setWidth(100, Unit.PERCENTAGE);
            infoPanel.setContent(text);
            addComponent(infoPanel);
            setSizeFull();
        } else {
            navigator.navigateTo(UIConstants.VIEW_REPORTLIST);
        }
    }
}
