package com.alu.tat.view;

import com.alu.tat.component.HSeparator;
import com.alu.tat.component.TaskComponentFactory;
import com.alu.tat.entity.Folder;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.User;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.FolderService;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.alu.tat.util.SessionHelper;
import com.alu.tat.util.TaskPresenter;
import com.alu.tat.util.UIComponentFactory;
import com.vaadin.data.Property;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.*;

/**
 * Created by imalolet on 6/10/2015.
 * Author Igor Maloletniy & Vasily Khodyrev
 */
public class TaskView extends AbstractActionView {

    private final static Logger logger =
            LoggerFactory.getLogger(TaskView.class);

    private Navigator navigator;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final boolean isCreate = isCreate(event.getParameters());
        final Long updateId = getUpdateId(event.getParameters());

        navigator = getUI().getNavigator();

        final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        //Left section begin
        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);

        final TextArea taskName = new TextArea("Task Name");
        taskName.setWidth("100%");
        taskName.setHeight("60px");
        taskName.addValidator(new StringLengthValidator(
                "Task name must not be empty", 1, 255, false));
        taskName.setWordwrap(true);
        final TextField taskAuth = new TextField("Author");
        taskAuth.setValue(SessionHelper.getCurrentUser(getSession()).getName());
        taskAuth.setEnabled(false);
        final TextArea taskDesc = new TextArea("Description");
        taskDesc.setWidth("100%");
        taskDesc.setWordwrap(true);
        final ComboBox taskRel = new ComboBox("Folder");
        taskRel.addItems(FolderService.getFolders());
        taskRel.setNullSelectionAllowed(false);
        Collection<Schema> schemas = Collections.EMPTY_LIST;
        if (isCreate) {
            schemas = SchemaService.getNotDeprecatedSchemas();
        } else {
            schemas = SchemaService.getSchemas();
        }
        final ComboBox taskSchema = new ComboBox("Schema", schemas);
        Schema defaultSchema = SchemaService.getDefaultSchema();
        if (defaultSchema == null) {
            defaultSchema = schemas.iterator().next();
        }
        taskSchema.setValue(defaultSchema);
        taskSchema.setNullSelectionAllowed(false);

        final ComboBox taskStatus = new ComboBox("Status", Arrays.asList(Task.Status.values()));
        taskStatus.setNullSelectionAllowed(false);
        taskStatus.setValue(Task.Status.NEW);
        taskStatus.setEnabled(false);

        form.addComponent(taskName);
        form.addComponent(taskAuth);
        form.addComponent(taskDesc);
        form.addComponent(taskRel);
        form.addComponent(taskSchema);
        form.addComponent(taskStatus);

        Button createButton = UIComponentFactory.getButton(isCreate ? "Create" : "Update", "TASKVIEW_CREATEORUPDATE_BUTTON", FontAwesome.PLUS);
        Button cancelButton = UIComponentFactory.getButton("Back", "TASKVIEW_CANCEL_BUTTON", FontAwesome.ARROW_LEFT);

        HorizontalLayout buttonGroup = new HorizontalLayout(createButton, new HSeparator(20), cancelButton);
        form.addComponent(buttonGroup);
        hsplit.setFirstComponent(form);
        //Left section end

        //Right section begin
        Schema curSchema = !isCreate ? TaskService.getTask(updateId).getSchema() : (Schema) taskSchema.getValue();
        final Map<String, Property> fieldMap = new HashMap<>();
        TabSheet ts = prepareTabDataView(fieldMap, curSchema);
        ts.setSizeUndefined();
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(true);
        vl.addComponent(ts);
        Panel panel = new Panel();

        panel.setSizeFull();
        panel.setContent(vl);
        hsplit.setSecondComponent(panel);
        //Right section end

        // Set the position of the splitter as percentage
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);
        hsplit.setSizeFull();
        addComponent(hsplit);
        this.setSizeFull();

        createButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!isDataValid(fieldMap)) return;
                Task t;
                if (isCreate) {
                    t = new Task();
                } else {
                    t = TaskService.getTask(updateId);
                }
                if (taskName.isValid()) {
                    t.setName(taskName.getValue());
                } else {
                    return;
                }
                t.setAuthor((User) getSession().getAttribute("user"));
                t.setDescription(taskDesc.getValue());
                if (taskRel.getValue() instanceof Folder) {
                    t.setFolder((Folder) taskRel.getValue());
                }
                t.setSchema((Schema) taskSchema.getValue());
                t.setStatus((Task.Status) taskStatus.getValue());
                t.setData(TaskPresenter.convertToData((Schema) taskSchema.getValue(), fieldMap));
                if (!isCreate) {
                    TaskService.updateTask(t);
                    SessionHelper.notifyAllUsers("Task '" + t.getName() + "' is updated");
                    logger.debug("User " + SessionHelper.getCurrentUser(getSession()) + " updated task '" + t.getName() + "'");
                } else {
                    TaskService.addTask(t);
                    SessionHelper.notifyAllUsers("Task '" + t.getName() + "' is created.");
                    logger.debug("User " + SessionHelper.getCurrentUser(getSession()) + " created task '" + t.getName() + "'");
                }

                navigator.navigateTo(UIConstants.VIEW_MAIN);
                Notification.show("Task '" + t.getName() + "' is successfully " + (isCreate ? "created" : "updated"), Notification.Type.TRAY_NOTIFICATION);
            }
        });

        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (isDataModified(fieldMap)) {
                    ConfirmDialog.show(getUI(), "All changes will be lost. Are you sure?", new ConfirmDialog.Listener() {
                        @Override
                        public void onClose(ConfirmDialog confirmDialog) {
                            if (confirmDialog.isConfirmed()) {
                                navigator.navigateTo(UIConstants.VIEW_MAIN);
                            }
                        }
                    });
                } else {
                    navigator.navigateTo(UIConstants.VIEW_MAIN);
                }
            }
        });

        //load task fields if its for edit
        if (!isCreate) {
            Task task = TaskService.getTask(updateId);
            taskName.setValue(String.valueOf(task.getName()));
            taskAuth.setValue(task.getAuthor().getName());
            taskDesc.setValue(task.getDescription());
            taskRel.setValue(task.getFolder());
            taskStatus.setValue(task.getStatus());
            taskStatus.setEnabled(true);
            taskSchema.setValue(task.getSchema());
            taskSchema.setEnabled(false);
            boolean isFinished = Task.Status.DONE.equals(task.getStatus());
            if (isFinished) {
                taskName.setEnabled(false);
                taskRel.setEnabled(false);
                if (!SessionHelper.getCurrentUser(getSession()).getIsSystem()) {
                    taskStatus.setEnabled(false);
                    createButton.setEnabled(false);
                }
            }
            initSchemaData(fieldMap, task.getData(), (Schema) taskSchema.getValue(), isFinished);
        }

        taskSchema.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Schema newSchema = (Schema) taskSchema.getValue();
                TabSheet ts = prepareTabDataView(fieldMap, newSchema);
                hsplit.setSecondComponent(ts);
                if (!isCreate) {
                    Task task = TaskService.getTask(updateId);
                    initSchemaData(fieldMap, task.getData(), newSchema, Task.Status.DONE.equals(task.getStatus()));
                }
            }
        });
    }

    private boolean isDataValid(Map<String, Property> fieldMap) {
        for (Map.Entry<String, Property> entry : fieldMap.entrySet()) {
            if (entry.getValue() instanceof AbstractField) {
                AbstractField af = (AbstractField) entry.getValue();
                if (!af.isValid()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isDataModified(Map<String, Property> fieldMap) {
        for (Map.Entry<String, Property> entry : fieldMap.entrySet()) {
            if (entry.getValue() instanceof AbstractField) {
                AbstractField af = (AbstractField) entry.getValue();
                if (af.isModified()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initSchemaData(final Map<String, Property> fieldMap, String jsonData, Schema schema, boolean isReadonly) {
        Map<String, Object> valueMap = TaskPresenter.convertFromJSON(jsonData, schema);
        for (String fieldName : fieldMap.keySet()) {
            Property field = fieldMap.get(fieldName);
            Object vo = valueMap.get(fieldName);
            if (vo != null) {
                field.setValue(vo);
            }
            if (isReadonly) {
                field.setReadOnly(isReadonly);
            }
        }
    }

    private TabSheet prepareTabDataView(Map<String, Property> fieldMap, Schema curSchema) {
        TabSheet ts = new TabSheet();
        FormLayout curForm = new FormLayout();
        curForm.setSizeFull();
        String tabName = "General";

        for (SchemaElement se : curSchema.getElementsList()) {
            final AbstractField c;
            switch (se.getType()) {
                case DOMAIN: {
                    if (curForm.getComponentCount() > 0) {
                        curForm.setSizeFull();
                        ts.addTab(curForm, tabName);
                    }
                    curForm = new FormLayout();
                    tabName = se.getName();
                    continue;
                }
                default: {
                    c = TaskComponentFactory.getField(se);
                    curForm.addComponent(c);
                    fieldMap.put(se.getName(), c);
                    break;
                }
            }
        }
        if (curForm.getComponentCount() > 0) {
            ts.addTab(curForm, tabName);
        }
        return ts;
    }
}
