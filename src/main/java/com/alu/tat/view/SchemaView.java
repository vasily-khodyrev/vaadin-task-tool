package com.alu.tat.view;

import com.alu.tat.Main;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.alu.tat.service.UserService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/8/2015
 */
public class SchemaView extends VerticalLayout implements View {
    private Navigator navigator;
    private SchemaService schemaService = SchemaService.getInstance();
    private Grid grid = new Grid();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Schema schema = (Schema) getSession().getAttribute("schema");
        if (schema == null) {
            schema = new Schema();
            getSession().setAttribute("schema", schema);
        }
        final Long id = schema.getId();

        navigator = getUI().getNavigator();

        VerticalLayout form = new VerticalLayout();
        final TextField schemaName = new TextField("Name");
        final TextField schemaDesc = new TextField("Description");
        configureGrid(grid, id);

        form.addComponent(schemaName);
        form.addComponent(schemaDesc);
        HorizontalLayout hl = new HorizontalLayout();
        Button addElement = new Button("Add item");
        final Button removeSelected = new Button("Remove");
        removeSelected.setEnabled(false);
        hl.addComponent(addElement);
        hl.addComponent(removeSelected);
        form.addComponent(hl);
        form.addComponent(grid);
        grid.addSelectionListener(new SelectionEvent.SelectionListener() {
                                      @Override
                                      public void select(SelectionEvent event) {
                                          removeSelected.setEnabled(true);
                                      }


                                  }
        );
        addElement.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                grid.getContainerDataSource().addItem(new SchemaElement());
            }
        });
        removeSelected.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // Delete all selected data items
                for (Object itemId : grid.getSelectedRows())
                    grid.getContainerDataSource().removeItem(itemId);

                // Disable after deleting
                event.getButton().setEnabled(false);
            }

        });
        Button create = new Button(id != null ? "Save" : "Create");
        Button back = new Button("Back");

        HorizontalLayout buttonGroup = new HorizontalLayout(create, back);
        form.addComponent(buttonGroup);
        form.setExpandRatio(grid, 4);
        //load task fields if its for edit
        if (id != null) {
            //getSession().setAttribute("schema", null);
            //Schema schema = schemaService.getSchema(id);
            schemaName.setValue(schema.getName());
            schemaDesc.setValue(schema.getDescription());
        }

        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Schema t = (Schema) getSession().getAttribute("schema");
                t.setName(schemaName.getValue());
                t.setDescription(schemaDesc.getValue());
                List<SchemaElement> newlist = new LinkedList<SchemaElement>();
                for (Object id : grid.getContainerDataSource().getItemIds()) {
                    Item item = grid.getContainerDataSource().getItem(id);
                    item.getItemPropertyIds();
                    SchemaElement el = (SchemaElement) id;
                    newlist.add(el);
                }
                t.setElementsList(newlist);
                if (id != null) {
                    t.setId(id);
                    schemaService.updateSchema(t);
                } else {
                    schemaService.addSchema(t);
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

    private void configureGrid(Grid grid, Long id) {
        List<SchemaElement> elems = Collections.EMPTY_LIST;
        if (id != null) {
            Schema schema = schemaService.getSchema(id);
            elems = schema.getElementsList();
        }
        grid.setSizeFull();
        final BeanItemContainer<SchemaElement> container = new BeanItemContainer<>(SchemaElement.class, elems);

        grid.setContainerDataSource(container);
        grid.setColumnOrder("type", "name", "description");
        grid.setEditorEnabled(true);
        grid.addItemClickListener(new SchemaElementClickListener());
    }

    private class SchemaElementClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            if (!(event.getItemId() instanceof SchemaElement)) {
                System.out.println("Not an SchemaElement instance - exiting");
                return;
            }
            /*if (event.isDoubleClick()) {
                final SchemaElement task = (SchemaElement) event.getItemId();
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
            }*/
        }
    }
}
