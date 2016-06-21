package com.alu.tat.view;

import com.alu.tat.entity.Folder;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.User;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.service.FolderService;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.alu.tat.service.UserService;
import com.alu.tat.util.SchemaPresenter;
import com.alu.tat.util.SessionHelper;
import com.alu.tat.util.TaskPresenter;
import com.alu.tat.util.UIComponentFactory;
import com.alu.tat.view.menu.*;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by imalolet on 6/10/2015.
 */
public class MainView extends VerticalLayout implements View {

    public static final ThemeResource FOLDER_ICON = new ThemeResource("../runo/icons/16/folder.png");
    private Navigator navigator;

    private TaskService taskService = TaskService.getInstance();
    private SchemaService schemaService = SchemaService.getInstance();

    final Grid taskGrid = new Grid();
    final Panel infoPanel = new Panel("Info");
    final Tree taskTree = new Tree();
    final Tree schemaTree = new Tree();
    final Tree usersTree = new Tree();
    final PopupMenuManager popupManager;

    public MainView() {
        super();
        popupManager = new PopupMenuManager(this);
    }

    private User getCurUser() {
        return SessionHelper.getCurrentUser(getSession());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        navigator = getUI().getNavigator();

        Panel rightPanel = getRightPanel();
        rightPanel.setSizeFull();

        Panel leftPanel = getLeftPanel(getCurUser().getIsSystem());
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

    private Component getUsersTreeMenu() {
        Layout container = new VerticalLayout();

        configureUsersTree(usersTree, infoPanel);

        container.addComponent(usersTree);

        return container;
    }

    private Panel getLeftPanel(boolean isSystemUser) {
        TabSheet tabsheet = new TabSheet();
        tabsheet.addTab(getTasksTreeMenu(), "Tasks", new ThemeResource("../runo/icons/16/document-txt.png"));
        TabSheet.Tab schemas = tabsheet.addTab(getSchemasTreeMenu(), "Schemas", new ThemeResource("../runo/icons/16/document.png"));
        TabSheet.Tab users = tabsheet.addTab(getUsersTreeMenu(), "Users", new ThemeResource("../runo/icons/16/users.png"));
        tabsheet.addTab(new VerticalLayout());
        if (!isSystemUser) {
            schemas.setVisible(false);
            users.setVisible(false);
        }

        Panel p = new Panel();
        HorizontalLayout panelCaption = new HorizontalLayout();
        panelCaption.setStyleName("panelstyle");
        panelCaption.setWidth(100, Unit.PERCENTAGE);
        Label menuLab = new Label("Menu");
        panelCaption.addComponent(menuLab);
        panelCaption.setComponentAlignment(menuLab, Alignment.TOP_LEFT);
        Button signout = UIComponentFactory.getButton("Sign Out", "MAINVIEW_SIGNOUT_BUTTON");
        signout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // "Logout" the user
                getSession().setAttribute("user", null);

                // Refresh this view, should redirect to login view
                navigator.navigateTo("");
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

        configureGrid(taskGrid, infoPanel);

        HorizontalLayout buttonPanel = new HorizontalLayout();

        final Button createButton = UIComponentFactory.getButton("Create task", "MAINVIEW_CREATE_TASK_BUTTON");
        final Button deleteTaskButton = UIComponentFactory.getButton("Delete task", "MAINVIEW_DEL_TASK_BUTTON");

        buttonPanel.addComponent(createButton);
        buttonPanel.addComponent(deleteTaskButton);
        buttonPanel.setComponentAlignment(deleteTaskButton, Alignment.TOP_RIGHT);


        container.addComponent(buttonPanel);
        container.addComponent(taskGrid);
        container.addComponent(infoPanel);

        container.setExpandRatio(buttonPanel, 2);
        container.setExpandRatio(taskGrid, 4);
        container.setExpandRatio(infoPanel, 2);

        taskGrid.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                if (!taskGrid.getSelectedRows().isEmpty()) {
                    deleteTaskButton.setEnabled(true);
                } else {
                    deleteTaskButton.setEnabled(false);
                }
            }
        });
        //use fontawsome icons to be cool looking
        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
        createButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(UIConstants.TASK_CREATE);
            }
        });

        deleteTaskButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Task t = (Task) taskGrid.getSelectedRow();
                taskService.removeTask(t.getId());
                navigator.navigateTo(UIConstants.VIEW_MAIN);
            }
        });

        return new Panel("Content", container);
    }

    private void configureTaskTree(Tree tree, Panel infoPanel) {
        final Collection<Task> tasks = taskService.getTasks();

        String root = "Category";

        tree.addItem(root);
        tree.expandItem(root);
        tree.setItemIcon(root, FOLDER_ICON);

        Set<Folder> foldersSet = new LinkedHashSet<>(FolderService.getFolders());
        //At first add all sub-nodes
        for (Folder r : foldersSet) {
            Item i = tree.addItem(r);
            tree.setItemIcon(r, FOLDER_ICON);
        }
        //After we can setup the roots
        for (Folder r : foldersSet) {
            if (r.getRoot() != null) {
                Folder rRoot = r.getRoot();
                tree.setParent(r, rRoot);
            } else {
                tree.setParent(r, root);
            }
        }

        for (Task t : tasks) {
            tree.addItem(t);
            tree.setChildrenAllowed(t, false);
            Folder parent = t.getFolder();
            if (parent != null) {
                tree.setParent(t, parent);
            } else {
                tree.setParent(t, root);
            }
        }

        tree.addItemClickListener(new TaskTreeItemClickListener());

    }

    private void configureSchemaTree(Tree tree, Panel infoPanel) {
        final Collection<Schema> schemas = schemaService.getSchemas();

        String root = "Schemas";

        tree.addItem(root);
        tree.setItemIcon(root, FOLDER_ICON);
        tree.expandItem(root);

        for (Schema t : schemas) {
            tree.addItem(t);
            tree.setParent(t, root);
            tree.setChildrenAllowed(t, false);
        }

        tree.addItemClickListener(new SchemaTreeItemClickListener());

    }

    private void configureUsersTree(Tree tree, Panel infoPanel) {
        final Collection<User> users = UserService.getUsers();

        String root = "Users";

        tree.addItem(root);
        tree.setItemIcon(root, FOLDER_ICON);
        tree.expandItem(root);

        for (User t : users) {
            tree.addItem(t);
            tree.setParent(t, root);
            tree.setChildrenAllowed(t, false);
        }

        tree.addItemClickListener(new UserTreeItemClickListener());

    }

    private void configureGrid(Grid grid, final Panel infoPanel) {
        final Collection<Task> tasks = taskService.getTasks();

        grid.setSizeFull();
        final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);

        grid.setContainerDataSource(container);
        grid.setColumnOrder("name", "author", "updateTime");

        grid.removeColumn("id");
        grid.removeColumn("data");
        grid.removeColumn("isSystem");
        grid.removeColumn("description");

        grid.addItemClickListener(new TastItemGridClickListener());


    }


    private class TastItemGridClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (event.getItemId() instanceof Task) {
                final Task task = (Task) event.getItemId();
                if (event.isDoubleClick()) {
                    navigator.navigateTo(UIConstants.TASK_UPDATE + task.getId());
                } else {
                    if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                        popupManager.showWindow(event.getClientX(), event.getClientY(), new TaskPopupMenu(task));
                    } else {
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
    }

    private class TaskTreeItemClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            //Root element
            if (event.getItemId() instanceof String) {
                if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    getUI().getCurrent().getSession().setAttribute("selectedFolder", null);
                    popupManager.showWindow(event.getClientX(), event.getClientY(), new FolderPopupMenu(null));
                } else {
                    Collection<Task> tasks = taskService.getTasks();
                    final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                    taskGrid.setContainerDataSource(container);
                    taskGrid.markAsDirty();
                }
            }
            //Release Nodes
            if (event.getItemId() instanceof Folder) {
                Folder folder = (Folder) event.getItemId();
                if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    getUI().getCurrent().getSession().setAttribute("selectedFolder", folder);
                    popupManager.showWindow(event.getClientX(), event.getClientY(), new FolderPopupMenu(folder));
                } else {
                    List<Task> tasks = taskService.findTaskByFolder(folder);
                    final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                    taskGrid.setContainerDataSource(container);
                    taskGrid.markAsDirty();
                }
            }
            //Task leaf
            if (event.getItemId() instanceof Task) {
                final Task task = (Task) event.getItemId();
                if (event.isDoubleClick()) {
                    navigator.navigateTo(UIConstants.TASK_UPDATE + task.getId());
                } else {
                    if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                        popupManager.showWindow(event.getClientX(), event.getClientY(), new TaskPopupMenu(task));
                    } else {
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
    }

    private class SchemaTreeItemClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (event.getItemId() instanceof String) {
                if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    popupManager.showWindow(event.getClientX(), event.getClientY(), new SchemaPopupMenu(null));
                } else {
                    Collection<Task> tasks = taskService.getTasks();
                    final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                    taskGrid.setContainerDataSource(container);
                    taskGrid.markAsDirty();
                }
            } else if (event.getItemId() instanceof Schema) {
                if (event.isDoubleClick()) {
                    final Schema schema = (Schema) event.getItemId();
                    navigator.navigateTo(UIConstants.SCHEMA_UPDATE + schema.getId());
                } else {
                    final Schema schema = (Schema) event.getItemId();
                    if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                        popupManager.showWindow(event.getClientX(), event.getClientY(), new SchemaPopupMenu(schema));
                    } else {
                        List<Task> tasks = taskService.findTaskBySchema(schema);
                        final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                        taskGrid.setContainerDataSource(container);
                        taskGrid.markAsDirty();

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

    private class UserTreeItemClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (event.getItemId() instanceof String) {
                if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    popupManager.showWindow(event.getClientX(), event.getClientY(), new UserPopupMenu(null));
                } else {
                    Collection<Task> tasks = taskService.getTasks();
                    final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                    taskGrid.setContainerDataSource(container);
                    taskGrid.markAsDirty();
                }
            } else if (event.getItemId() instanceof User) {
                final User user = (User) event.getItemId();
                if (event.isDoubleClick()) {
                    navigator.navigateTo(UIConstants.USER_UPDATE + user.getId());
                } else {
                    if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                        popupManager.showWindow(event.getClientX(), event.getClientY(), new UserPopupMenu(user));
                    } else {
                        List<Task> tasks = taskService.findTaskByUser(user);
                        final BeanItemContainer<Task> container = new BeanItemContainer<>(Task.class, tasks);
                        taskGrid.setContainerDataSource(container);
                        taskGrid.markAsDirty();
                    }
                }
            }
        }
    }
}
