package com.alu.tat.view;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.alu.tat.util.SchemaPresenter;
import com.alu.tat.util.TaskPresenter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.List;

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
        tabsheet.addTab(getTasksTreeMenu(), "Tasks", new ThemeResource("../runo/icons/16/document-txt.png"));
        tabsheet.addTab(getSchemasTreeMenu(), "Schemas", new ThemeResource("../runo/icons/16/document.png"));
        tabsheet.addTab(new VerticalLayout());

        Panel p = new Panel();
        HorizontalLayout panelCaption = new HorizontalLayout();
        panelCaption.setStyleName("panelstyle");
        panelCaption.setWidth(100, Unit.PERCENTAGE);
        Label menuLab = new Label("Menu");
        panelCaption.addComponent(menuLab);
        panelCaption.setComponentAlignment(menuLab, Alignment.TOP_LEFT);
        Button signout = new Button("Sign Out");
        signout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().getSession().close();
                //navigator.navigateTo(UIConstants.VIEW_LOGIN);
            }
        });
        panelCaption.addComponent(signout);
        panelCaption.setComponentAlignment(signout, Alignment.TOP_RIGHT);
        panelCaption.setHeight(signout.getHeight(), signout.getHeightUnits());
        VerticalLayout v = new VerticalLayout();
        v.setMargin(true);
        v.addComponent(panelCaption);
        v.addComponent(tabsheet);
        v.setExpandRatio(tabsheet, 1);
        p.setContent(v);
        return p;
    }


    private Panel getRightPanel() {
        VerticalLayout container = new VerticalLayout();

        configureGrid(grid, infoPanel);

        HorizontalLayout buttonPanel = new HorizontalLayout();

        Button createButton = new Button("Create task");
        final Button deleteButton = new Button("Delete task");
        deleteButton.setEnabled(false);
        Button schemaButton = new Button("Create Schema");

        buttonPanel.addComponent(createButton);
        buttonPanel.addComponent(schemaButton);
        buttonPanel.addComponent(deleteButton);
        buttonPanel.setComponentAlignment(deleteButton,Alignment.TOP_RIGHT);


        container.addComponent(buttonPanel);
        container.addComponent(grid);
        container.addComponent(infoPanel);

        container.setExpandRatio(buttonPanel, 2);
        container.setExpandRatio(grid, 4);
        container.setExpandRatio(infoPanel, 2);

        grid.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                if (!grid.getSelectedRows().isEmpty()) {
                    deleteButton.setEnabled(true);
                } else {
                    deleteButton.setEnabled(false);
                }
            }
        });
        createButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(UIConstants.TASK_CREATE);
            }
        });

        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Task t = (Task) grid.getSelectedRow();
                taskService.removeTask(t.getId());
                navigator.navigateTo(UIConstants.VIEW_MAIN);
            }
        });

        schemaButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getSession().setAttribute("schema", null);
                navigator.navigateTo(UIConstants.SCHEMA_CREATE);
            }
        });

        return new Panel("Content", container);
    }

    private void configureTaskTree(Tree tree, Panel infoPanel) {
        final Collection<Task> tasks = taskService.getTasks();

        String root = "Release";

        tree.addItem(root);
        tree.expandItem(root);
        tree.setItemIcon(root, new ThemeResource("../runo/icons/16/folder.png"));

        for (Task.Release r : Task.Release.values()) {
            Item i = tree.addItem(r);
            i.getItemPropertyIds();
            tree.setParent(r, root);
            tree.setItemIcon(r, new ThemeResource("../runo/icons/16/folder.png"));
        }

        for (Task t : tasks) {
            Task.Release parent = t.getRelease();
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
        tree.setItemIcon(root, new ThemeResource("../runo/icons/16/folder.png"));
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

        grid.addItemClickListener(new TastItemClickListener());


    }


    private class TastItemClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            //Root element
            if (event.getItemId() instanceof String) {
                Collection<Task> tasks = taskService.getTasks();
                final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                grid.setContainerDataSource(container);
                grid.markAsDirty();
            }
            //Release Nodes
            if (event.getItemId() instanceof Task.Release) {
                List<Task> tasks = taskService.findTaskByRelease((Task.Release) event.getItemId());
                final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                grid.setContainerDataSource(container);
                grid.markAsDirty();
            }
            //Task leaf
            if (event.getItemId() instanceof Task) {
                if (event.isDoubleClick()) {
                    final Task task = (Task) event.getItemId();
                    getSession().setAttribute("item", task.getId());
                    navigator.navigateTo(UIConstants.TASK_UPDATE + task.getId());
                } else {
                    final Task task = (Task) event.getItemId();

                    RichTextArea text = new RichTextArea();
                    text.setSizeFull();
                    text.setValue(TaskPresenter.getHtmlView(task));
                    text.setReadOnly(true);

                    infoPanel.setSizeFull();
                    infoPanel.setContent(text);
                }
            }
        }
    }

    private class SchemaItemClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (event.getItemId() instanceof String) {
                Collection<Task> tasks = taskService.getTasks();
                final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                grid.setContainerDataSource(container);
                grid.markAsDirty();
            } else if (event.getItemId() instanceof Schema) {
                if (event.isDoubleClick()) {
                    final Schema schema = (Schema) event.getItemId();
                    getSession().setAttribute("schema", schema);
                    navigator.navigateTo(UIConstants.SCHEMA_UPDATE + schema.getId());
                } else {
                    final Schema schema = (Schema) event.getItemId();
                    List<Task> tasks = taskService.findTaskBySchema(schema);
                    final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                    grid.setContainerDataSource(container);
                    grid.markAsDirty();

                    RichTextArea text = new RichTextArea();
                    text.setSizeFull();
                    text.setValue(SchemaPresenter.getHtmlView(schema));
                    text.setReadOnly(true);

                    infoPanel.setSizeFull();
                    infoPanel.setContent(text);
                }
            }
        }
    }
}
