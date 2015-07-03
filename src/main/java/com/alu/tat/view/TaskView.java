package com.alu.tat.view;

import com.alu.tat.Main;
import com.alu.tat.entity.Task;
import com.alu.tat.service.TaskService;
import com.alu.tat.service.UserService;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by imalolet on 6/10/2015.
 */
public class TaskView extends VerticalLayout implements View {

    private Navigator navigator;
    private TaskService taskService = TaskService.getInstance();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final Long id = (Long) getSession().getAttribute("item");

        navigator = getUI().getNavigator();

        FormLayout form = new FormLayout();
        final TextField crId = new TextField("Crqms");
        final TextField author = new TextField("Author");
        final TextField descr = new TextField("Description");
        final ComboBox release = new ComboBox("Release");

        for (Task.Release r : Task.Release.values()) {
            release.addItem(r);
        }
        release.setNullSelectionAllowed(false);

        form.addComponent(crId);
        form.addComponent(author);
        form.addComponent(descr);
        form.addComponent(release);

        Button create = new Button(id != null ? "Save" : "Create");
        Button back = new Button("Back");

        HorizontalLayout buttonGroup = new HorizontalLayout(create, back);
        form.addComponent(buttonGroup);

        //load task fields if its for edit
        if (id != null) {
            getSession().setAttribute("item", null);
            Task task = taskService.getTask(id);
            crId.setValue(String.valueOf(task.getId()));
            author.setValue(UserService.currentUser().getName());
            descr.setValue(task.getDescription());
            release.setValue(task.getRelease());
        }

        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Task t = new Task();
                t.setName(crId.getValue());
                t.setAuthor(UserService.currentUser());
                t.setDescription(descr.getValue());
                t.setRelease((Task.Release) release.getValue());

                if (id != null) {
                    t.setId(id);
                    taskService.updateTask(t);
                } else {
                    taskService.addTask(t);
                }

                navigator.navigateTo(Main.MAIN_VIEW);
            }
        });

        back.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(Main.MAIN_VIEW);
            }
        });

        addComponent(form);
    }
}
