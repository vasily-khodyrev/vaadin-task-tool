package com.alu.tat.view;

import com.alu.tat.Main;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.TaskService;
import com.alu.tat.service.UserService;
import com.vaadin.data.Property;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by imalolet on 6/10/2015.
 */
public class TaskView extends VerticalLayout implements View {

    private Navigator navigator;
    private TaskService taskService = TaskService.getInstance();
    private SchemaService schemaService = SchemaService.getInstance();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final Long id = (Long) getSession().getAttribute("item");

        navigator = getUI().getNavigator();

        //TODO
        /*HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setFirstComponent(new Label("75% wide panel"));
        hsplit.setSecondComponent(new Label("25% wide panel"));
        // Set the position of the splitter as percentage
        hsplit.setSplitPosition(75, Sizeable.UNITS_PERCENTAGE);*/


        HorizontalLayout hl = new HorizontalLayout();
        Panel leftPanel = new Panel();
        FormLayout form = new FormLayout();
        final TextField crId = new TextField("Crqms");
        final TextField author = new TextField("Author");
        final TextField descr = new TextField("Description");
        final ComboBox release = new ComboBox("Release");

        for (Task.Release r : Task.Release.values()) {
            release.addItem(r);
        }
        release.setNullSelectionAllowed(false);

        final ComboBox schemaChoice = new ComboBox("Schema");
        Collection<Schema> schemas = schemaService.getSchemas();
        for (Schema s : schemas) {
            schemaChoice.addItem(s);
        }
        //TODO
        schemaChoice.setValue(schemas.iterator().next());
        schemaChoice.setNullSelectionAllowed(false);

        form.addComponent(crId);
        form.addComponent(author);
        form.addComponent(descr);
        form.addComponent(release);
        form.addComponent(schemaChoice);

        Button create = new Button(id != null ? "Update" : "Create");
        Button back = new Button("Back");

        HorizontalLayout buttonGroup = new HorizontalLayout(create, back);
        form.addComponent(buttonGroup);


        final Map<String, AbstractField> fieldMap = new HashMap<>();

        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Task t = new Task();
                t.setName(crId.getValue());
                t.setAuthor(UserService.currentUser());
                t.setDescription(descr.getValue());
                t.setRelease((Task.Release) release.getValue());
                t.setSchema((Schema) schemaChoice.getValue());
                t.setData(convertToData((Schema) schemaChoice.getValue(), fieldMap));
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
        Panel p1 = new Panel("General", form);
        p1.setSizeFull();
        hl.addComponent(p1);
        Schema curSchema = id != null? taskService.getTask(id).getSchema() :(Schema) schemaChoice.getValue();
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
        }
        ts.addTab(curForm, tabName);
        Panel p2 = new Panel("Data", ts);
        p2.setSizeFull();
        hl.addComponent(p2);
        hl.setSizeFull();

        addComponent(hl);

        //load task fields if its for edit
        if (id != null) {
            getSession().setAttribute("item", null);
            Task task = taskService.getTask(id);
            crId.setValue(String.valueOf(task.getName()));
            author.setValue(UserService.currentUser().getName());
            descr.setValue(task.getDescription());
            release.setValue(task.getRelease());

            if (task.getSchema() != null) {
                schemaChoice.setValue(task.getSchema());
            }

            Map<String, Object> valueMap = convertFromJSON(task.getData(), (Schema) schemaChoice.getValue());
            for (String fieldName : fieldMap.keySet()) {
                AbstractField field = fieldMap.get(fieldName);
                field.setValue(valueMap.get(fieldName));
            }
        }
    }

    private String convertToData(Schema schema, Map<String, AbstractField> fieldMap) {
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
