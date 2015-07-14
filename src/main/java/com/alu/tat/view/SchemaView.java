package com.alu.tat.view;

import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.Navigator;
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
public class SchemaView extends AbstractActionView {
    private Navigator navigator;
    private SchemaService schemaService = SchemaService.getInstance();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final boolean isCreate = isCreate(event.getParameters());
        final Long updateId = getUpdateId(event.getParameters());

        navigator = getUI().getNavigator();

        VerticalLayout form = new VerticalLayout();
        final TextField schemaName = new TextField("Name");
        final TextField schemaDesc = new TextField("Description");
        form.addComponent(schemaName);
        form.addComponent(schemaDesc);

        final Grid grid = prepareGrid(form);

        Button create = new Button(isCreate ? "Create" : "Save");
        Button back = new Button("Back");

        HorizontalLayout buttonGroup = new HorizontalLayout(create, back);
        form.addComponent(buttonGroup);
        form.setExpandRatio(grid, 4);

        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Schema t;
                if (isCreate) {
                    t = new Schema();
                } else {
                    t = schemaService.getSchema(updateId);
                }
                t.setName(schemaName.getValue());
                t.setDescription(schemaDesc.getValue());
                List<SchemaElement> newlist = t.getElementsList();
                Collection<SchemaElement> cse = (Collection<SchemaElement> ) grid.getContainerDataSource().getItemIds();
                newlist.clear();
                newlist.addAll(cse);
                if (!isCreate) {
                    schemaService.updateSchema(t);
                } else {
                    schemaService.addSchema(t);
                }

                navigator.navigateTo(UIConstants.VIEW_MAIN);
            }
        });

        back.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(UIConstants.VIEW_MAIN);
            }
        });

        addComponent(form);

        //load task fields if its for edit
        if (!isCreate) {
            Schema schema = schemaService.getSchema(updateId);
            schemaName.setValue(schema.getName());
            schemaDesc.setValue(schema.getDescription());
            for (SchemaElement se : schema.getElementsList()) {
                grid.getContainerDataSource().addItem(se);
            }
        }
    }

    private Grid prepareGrid(ComponentContainer cc) {
        final Grid grid = new Grid();
        configureGrid(grid);
        HorizontalLayout hl = new HorizontalLayout();
        Button addElement = new Button("Add item");
        final Button removeSelected = new Button("Remove");
        removeSelected.setEnabled(false);
        hl.addComponent(addElement);
        hl.addComponent(removeSelected);
        cc.addComponent(hl);
        cc.addComponent(grid);
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
        return grid;
    }

    private void configureGrid(Grid grid) {
        grid.setSizeFull();
        final BeanItemContainer<SchemaElement> container = new BeanItemContainer<>(SchemaElement.class);

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
                navigator.navigateTo(Main.VIEW_TASK);
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
