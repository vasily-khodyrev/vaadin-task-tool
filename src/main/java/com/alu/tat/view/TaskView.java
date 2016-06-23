package com.alu.tat.view;

import com.alu.tat.component.TaskComponentFactory;
import com.alu.tat.entity.Folder;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.User;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.FolderService;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.alu.tat.util.TaskPresenter;
import com.alu.tat.util.UIComponentFactory;
import com.vaadin.data.Property;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by imalolet on 6/10/2015.
 * Author Igor Maloletniy & Vasily Khodyrev
 */
public class TaskView extends AbstractActionView {

    private Navigator navigator;
    private TaskService taskService = TaskService.getInstance();
    private SchemaService schemaService = SchemaService.getInstance();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final boolean isCreate = isCreate(event.getParameters());
        final Long updateId = getUpdateId(event.getParameters());

        navigator = getUI().getNavigator();

        final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        //Left section begin
        FormLayout form = new FormLayout();
        final TextArea taskName = new TextArea("Task Name");
        taskName.setWidth("100%");
        taskName.setHeight("60px");
        taskName.addValidator(new StringLengthValidator(
                "Task name must not be empty", 1, 255, false));
        final TextField taskAuth = new TextField("Author");
        taskAuth.setValue(((User) getSession().getAttribute("user")).getName());
        taskAuth.setEnabled(false);
        final TextArea taskDesc = new TextArea("Description");
        taskDesc.setWidth("100%");
        taskDesc.setWordwrap(false);
        final ComboBox taskRel = new ComboBox("Folder");
        taskRel.addItems(FolderService.getFolders());
        taskRel.setNullSelectionAllowed(false);

        Collection<Schema> schemas = schemaService.getSchemas();
        final ComboBox taskSchema = new ComboBox("Schema", schemas);
        Schema defaultSchema = schemas.iterator().next();
        taskSchema.setValue(defaultSchema);
        taskSchema.setNullSelectionAllowed(false);

        form.addComponent(taskName);
        form.addComponent(taskAuth);
        form.addComponent(taskDesc);
        form.addComponent(taskRel);
        form.addComponent(taskSchema);

        Button create = UIComponentFactory.getButton(isCreate ? "Create" : "Update", "TASKVIEW_CREATEORUPDATE_BUTTON", FontAwesome.PLUS);
        Button back = UIComponentFactory.getButton("Back", "TASKVIEW_CANCEL_BUTTON", FontAwesome.ARROW_LEFT);

        HorizontalLayout buttonGroup = new HorizontalLayout(create, back);
        form.addComponent(buttonGroup);
        hsplit.setFirstComponent(form);
        //Left section end

        //Right section begin
        Schema curSchema = !isCreate ? taskService.getTask(updateId).getSchema() : (Schema) taskSchema.getValue();
        final Map<String, Property> fieldMap = new HashMap<>();
        TabSheet ts = prepareTabDataView(fieldMap, curSchema);
        hsplit.setSecondComponent(ts);
        //Right section end

        // Set the position of the splitter as percentage
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);
        hsplit.setSizeFull();
        addComponent(hsplit);

        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!isDataValid(fieldMap)) return;

                Task t;
                if (isCreate) {
                    t = new Task();
                } else {
                    t = taskService.getTask(updateId);
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
                t.setData(TaskPresenter.convertToData((Schema) taskSchema.getValue(), fieldMap));
                if (!isCreate) {
                    taskService.updateTask(t);
                } else {
                    taskService.addTask(t);
                }

                navigator.navigateTo(UIConstants.VIEW_MAIN);
                Notification.show("Task '" + t.getName() + "' is successfully " + (isCreate ? "created" : "updated"), Notification.Type.TRAY_NOTIFICATION);
            }
        });

        back.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(UIConstants.VIEW_MAIN);
            }
        });

        //load task fields if its for edit
        if (!isCreate) {
            Task task = taskService.getTask(updateId);
            taskName.setValue(String.valueOf(task.getName()));
            taskAuth.setValue(task.getAuthor().getName());
            taskDesc.setValue(task.getDescription());
            taskRel.setValue(task.getFolder());
            taskSchema.setValue(task.getSchema());
            initSchemaData(fieldMap, task.getData(), (Schema) taskSchema.getValue());
        }

        taskSchema.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Schema newSchema = (Schema) taskSchema.getValue();
                TabSheet ts = prepareTabDataView(fieldMap, newSchema);
                hsplit.setSecondComponent(ts);
                if (!isCreate) {
                    Task task = taskService.getTask(updateId);
                    initSchemaData(fieldMap, task.getData(), newSchema);
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

    private void initSchemaData(final Map<String, Property> fieldMap, String jsonData, Schema schema) {
        Map<String, Object> valueMap = TaskPresenter.convertFromJSON(jsonData, schema);
        for (String fieldName : fieldMap.keySet()) {
            Property field = fieldMap.get(fieldName);
            Object vo = valueMap.get(fieldName);
            if (vo != null) {
                field.setValue(vo);
            }
        }
    }

    private TabSheet prepareTabDataView(Map<String, Property> fieldMap, Schema curSchema) {
        TabSheet ts = new TabSheet();
        FormLayout curForm = new FormLayout();
        curForm.setWidth("100%");
        String tabName = "General";

        for (SchemaElement se : curSchema.getElementsList()) {
            final AbstractField c;
            switch (se.getType()) {
                case DOMAIN: {
                    if (curForm.getComponentCount() > 0) {
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
