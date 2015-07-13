package com.alu.tat.view;

import java.util.Collection;

import com.alu.tat.Main;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;

/**
 * Created by imalolet on 6/10/2015.
 */
public class MainView extends VerticalLayout implements View {

    private Navigator navigator;

    private TaskService taskService = TaskService.getInstance();
    private SchemaService schemaService = SchemaService.getInstance();

    final Grid grid = new Grid();
    final Panel infoPanel = new Panel("Info");
    final Tree taskTree = new Tree();
    final Tree schemaTree = new Tree();


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        navigator = getUI().getNavigator();

        Panel rightPanel = getRightPanel();
        rightPanel.setSizeFull();
        Panel leftPanel = getLeftPanel();
        leftPanel.setSizeFull();

        HorizontalLayout container = new HorizontalLayout(leftPanel, rightPanel);

        container.setExpandRatio(leftPanel, 1);
        container.setExpandRatio(rightPanel, 4);
        container.setSizeFull();

        addComponent(container);
        setExpandRatio(container, 1);
        setSizeFull();

    }

    private Component getTasksTreeMenu() {
        Layout container = new VerticalLayout();

        configureTaskTree(taskTree, infoPanel);

        container.addComponent(taskTree);

        return container;
    }

    private Component getSchemasTreeMenu() {
        Layout container = new VerticalLayout();

        configureSchemaTree(schemaTree, infoPanel);

        container.addComponent(schemaTree);

        return container;
    }

    private Panel getLeftPanel() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.addTab(getTasksTreeMenu(),"Tasks");
        tabsheet.addTab(getSchemasTreeMenu(),"Schemas");
        tabsheet.addTab(new VerticalLayout());

        return new Panel("Menu",tabsheet);
    }


    private Panel getRightPanel() {
        VerticalLayout container = new VerticalLayout();

        configureGrid(grid, infoPanel);

        HorizontalLayout buttonPanel = new HorizontalLayout();

        Button loginButton = new Button("Sign in");
        Button createButton = new Button("Create task");
        Button deleteButton = new Button("Delete task");
        Button schemaButton = new Button("Create Schema");

        buttonPanel.addComponent(loginButton);
        buttonPanel.addComponent(createButton);
        buttonPanel.addComponent(deleteButton);
        buttonPanel.addComponent(schemaButton);


        container.addComponent(buttonPanel);
        container.addComponent(grid);
        container.addComponent(infoPanel);

        container.setExpandRatio(buttonPanel, 2);
        container.setExpandRatio(grid, 4);
        container.setExpandRatio(infoPanel, 2);


        createButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(Main.CREATE_VIEW);
            }
        });

        loginButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(Main.LOGIN_VIEW);
            }
        });

        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Task t = (Task) grid.getSelectedRow();
                taskService.removeTask(t.getId());
                navigator.navigateTo(Main.MAIN_VIEW);
            }
        });

        schemaButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getSession().setAttribute("schema",null);
                navigator.navigateTo(Main.CREATE_SCHEMA);
            }
        });

        return new Panel("Content", container);
    }

    private void configureTaskTree(Tree tree, Panel infoPanel) {
        final Collection<Task> tasks = taskService.getTasks();

        String root = "Release";

        tree.addItem(root);
        tree.expandItem(root);

        for (Task.Release r : Task.Release.values()) {
            tree.addItem(r.getVersion());
            tree.setParent(r.getVersion(), root);
        }

        for (Task t : tasks) {
            String parent = t.getRelease().getVersion();
            tree.addItem(t);
            tree.setChildrenAllowed(t, false);
            tree.setParent(t, parent);
        }

        tree.addItemClickListener(new TastItemClickListener());

    }

    private void configureSchemaTree(Tree tree, Panel infoPanel) {
        final Collection<Schema> schemas = schemaService.getSchemas();

        String root = "Schemas";

        tree.addItem(root);
        tree.expandItem(root);

        for (Schema t : schemas) {
            tree.addItem(t);
            tree.setParent(t, root);
            tree.setChildrenAllowed(t, false);
        }

        tree.addItemClickListener(new SchemaItemClickListener());

    }

    private void configureGrid(Grid grid, final Panel infoPanel) {
        final Collection<Task> tasks = taskService.getTasks();

        grid.setSizeFull();
        final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);

        grid.setContainerDataSource(container);
        grid.setColumnOrder("name", "author", "description");

        grid.removeColumn("id");
        grid.removeColumn("updateTime");

        grid.addItemClickListener(new TastItemClickListener());


    }


    private class TastItemClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (!(event.getItemId() instanceof Task)) {
                System.out.println("Not an Task instance - exiting");
                return;
            }
            if (event.isDoubleClick()) {
                final Task task = (Task) event.getItemId();
                getSession().setAttribute("item", task.getId());
                navigator.navigateTo(Main.CREATE_VIEW);
            } else {
                final Task task = (Task) event.getItemId();
                VerticalLayout container = new VerticalLayout();

                Label name = new Label("Name: " + task.getName());
                Label release = new Label("Release: " + task.getRelease().getVersion());
                Label descr = new Label("Description: " + task.getDescription());

                container.addComponent(name);
                container.addComponent(release);
                container.addComponent(descr);

                infoPanel.setSizeFull();
                infoPanel.setContent(container);
            }
        }
    }

    private class SchemaItemClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (!(event.getItemId() instanceof Schema)) {
                System.out.println("Not an Schema instance - exiting");
                return;
            }
            if (event.isDoubleClick()) {
                final Schema schema = (Schema) event.getItemId();
                getSession().setAttribute("schema", schema);
                navigator.navigateTo(Main.CREATE_SCHEMA);
            } else {
                final Schema schema = (Schema) event.getItemId();
                VerticalLayout container = new VerticalLayout();

                Label name = new Label("Name: " + schema.getName());
                Label descr = new Label("Description: " + schema.getDescription());

                container.addComponent(name);
                container.addComponent(descr);

                infoPanel.setSizeFull();
                infoPanel.setContent(container);
            }
        }
    }
}
