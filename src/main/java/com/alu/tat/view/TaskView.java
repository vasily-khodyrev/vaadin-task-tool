package com.alu.tat.view;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.alu.tat.service.UserService;
import com.vaadin.data.Property;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.util.Arrays;
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
        final TextField taskName = new TextField("Task Name");
        final TextField taskAuth = new TextField("Author");
        final TextField taskDesc = new TextField("Description");
        final ComboBox taskRel = new ComboBox("Release", Arrays.asList(Task.Release.values()));
        taskRel.select(Task.Release.OT11);
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

        Button create = new Button(isCreate ? "Create" : "Update");
        Button back = new Button("Back");

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
                Task t;
                if (isCreate) {
                    t = new Task();
                } else {
                    t = taskService.getTask(updateId);
                }
                t.setName(taskName.getValue());
                t.setAuthor(UserService.currentUser());
                t.setDescription(taskDesc.getValue());
                t.setRelease((Task.Release) taskRel.getValue());
                t.setSchema((Schema) taskSchema.getValue());
                t.setData(convertToData((Schema) taskSchema.getValue(), fieldMap));
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
            taskAuth.setValue(UserService.currentUser().getName());
            taskDesc.setValue(task.getDescription());
            taskRel.setValue(task.getRelease());
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

    private void initSchemaData(final Map<String, Property> fieldMap, String jsonData, Schema schema) {
        Map<String, Object> valueMap = convertFromJSON(jsonData, schema);
        for (String fieldName : fieldMap.keySet()) {
            Property field = fieldMap.get(fieldName);
            field.setValue(valueMap.get(fieldName));
        }
    }

    private TabSheet prepareTabDataView(Map<String, Property> fieldMap, Schema curSchema) {
        TabSheet ts = new TabSheet();
        FormLayout curForm = new FormLayout();
        String tabName = "General";

        for (SchemaElement se : curSchema.getElementsList()) {
            AbstractField c = new TextField();
            switch (se.getType()) {
                case BOOLEAN: {
                    c = new CheckBox(se.getName());
                    curForm.addComponent(c);
                    fieldMap.put(se.getName(), c);
                    break;
                }
                case INTEGER: {
                    c = new TextField(se.getName());
                    c.setConverter(Integer.class);
                    curForm.addComponent(c);
                    fieldMap.put(se.getName(), c);
                    break;
                }
                case STRING: {
                    c = new TextField(se.getName());
                    curForm.addComponent(c);
                    fieldMap.put(se.getName(), c);
                    break;
                }
                case DOMAIN: {
                    if (curForm.getComponentCount() > 0) {
                        ts.addTab(curForm, tabName);
                    }
                    curForm = new FormLayout();
                    tabName = se.getName();
                    continue;
                }
                default: {
                    curForm.addComponent(c);
                    fieldMap.put(se.getName(), c);
                    break;
                }
            }
            c.setDescription(se.getDescription());
        }
        if (curForm.getComponentCount() > 0) {
            ts.addTab(curForm, tabName);
        }
        return ts;
    }

    private String convertToData(Schema schema, Map<String, Property> fieldMap) {
        JSONObject json = new JSONObject();
        for (SchemaElement se : schema.getElementsList()) {

            switch (se.getType()) {
                case DOMAIN:
                    break;
                case INTEGER:
                case STRING:
                case BOOLEAN:
                default: {
                    Object value = fieldMap.get(se.getName()).getValue();
                    json.put(se.getName(), value);
                    break;
                }
            }

        }
        return json.toString();
    }

    private Map<String, Object> convertFromJSON(String data, Schema schema) {
        Map<String, Object> result = new HashMap<>();
        JSON json = JSONSerializer.toJSON(data);
        if (json instanceof JSONObject) {
            JSONObject jso = (JSONObject) json;
            for (SchemaElement se : schema.getElementsList()) {
                if (jso.has(se.getName())) {
                    switch (se.getType()) {
                        case DOMAIN:
                            break;
                        case INTEGER: {
                            String value = jso.getString(se.getName());
                            String o = value;
                            result.put(se.getName(), o);
                            break;
                        }
                        case STRING: {
                            String value = jso.getString(se.getName());
                            String o = value;
                            result.put(se.getName(), o);
                            break;
                        }
                        case BOOLEAN: {
                            String value = jso.getString(se.getName());
                            Boolean o = Boolean.valueOf(value);
                            result.put(se.getName(), o);
                            break;
                        }
                        default:
                            break;

                    }
                }

            }
        }
        return result;
    }
}
