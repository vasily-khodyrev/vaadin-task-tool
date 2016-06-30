package com.alu.tat.view;

import com.alu.tat.component.VSeparator;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.alu.tat.util.UIComponentFactory;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/8/2015
 */
public class SchemaView extends AbstractActionView {
    private Navigator navigator;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final boolean isCreate = isCreate(event.getParameters());
        final Long updateId = getUpdateId(event.getParameters());

        navigator = getUI().getNavigator();

        VerticalLayout form = new VerticalLayout();
        final TextArea schemaName = new TextArea("Name");
        schemaName.setRows(2);
        schemaName.addValidator(new StringLengthValidator(
                "Schema name must not be empty", 1, 255, false));
        final TextArea schemaDesc = new TextArea("Description");
        final CheckBox isDefault = new CheckBox("Is Default");
        isDefault.setDescription("Put default if you want this Schema to be set by default for the new tasks");
        final CheckBox isDeprecated = new CheckBox("Is Deprecated");
        isDeprecated.setDescription("Put deprecated if you do not want this schema to be used for Task analysis");
        form.addComponent(schemaName);
        form.addComponent(schemaDesc);
        form.addComponent(new VSeparator(20));
        form.addComponent(isDefault);
        form.addComponent(new VSeparator(20));
        form.addComponent(isDeprecated);
        form.addComponent(new VSeparator(20));

        final Grid grid = prepareGrid(form);

        Button create = UIComponentFactory.getButton(isCreate ? "Create" : "Update", "SCHEMAVIEW_CREATEORUPDATE_BUTTON", new ThemeResource(("../runo/icons/16/ok.png")));
        Button back = UIComponentFactory.getButton("Back", "SCHEMAVIEW_CANCEL_BUTTON", new ThemeResource(("../runo/icons/16/cancel.png")));

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
                    t = SchemaService.getSchema(updateId);
                }
                t.setName(schemaName.getValue());
                t.setDescription(schemaDesc.getValue());
                t.setIsdefault(isDefault.getValue());
                t.setDeprecated(isDeprecated.getValue());
                List<SchemaElement> newlist = t.getElementsList();
                Collection<SchemaElement> cse = (Collection<SchemaElement>) grid.getContainerDataSource().getItemIds();
                newlist.clear();
                newlist.addAll(cse);
                if (!isCreate) {
                    SchemaService.updateSchema(t);
                } else {
                    SchemaService.addSchema(t);
                }

                navigator.navigateTo(UIConstants.VIEW_MAIN);
                Notification.show("Schema '" + t.getName() + "' is successfully " + (isCreate ? "created" : "updated"), Notification.Type.TRAY_NOTIFICATION);
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
            Schema schema = SchemaService.getSchema(updateId);
            schemaName.setValue(schema.getName());
            schemaDesc.setValue(schema.getDescription());
            isDefault.setValue(schema.getIsdefault() != null ? schema.getIsdefault() : false);
            isDeprecated.setValue(schema.getDeprecated());
            for (SchemaElement se : schema.getElementsList()) {
                grid.getContainerDataSource().addItem(se);
            }
        }
    }

    private Grid prepareGrid(ComponentContainer cc) {
        final Grid grid = new Grid();
        configureGrid(grid);
        HorizontalLayout buttonGroup = new HorizontalLayout();
        Button addButton = UIComponentFactory.getButton("Add item", "SCHEMAVIEW_ADDITEM_BUTTON");
        addButton.setIcon(FontAwesome.PLUS_CIRCLE);
        final Button removeButton = UIComponentFactory.getButton("Remove", "SCHEMAVIEW_REMOVEITEM_BUTTON");
        removeButton.setIcon(FontAwesome.MINUS_CIRCLE);
        removeButton.setEnabled(false);
        buttonGroup.addComponent(addButton);
        buttonGroup.addComponent(removeButton);
        cc.addComponent(buttonGroup);
        cc.addComponent(grid);
        grid.addSelectionListener(new SelectionEvent.SelectionListener() {
                                      @Override
                                      public void select(SelectionEvent event) {
                                          if (!grid.getSelectedRows().isEmpty()) {
                                              removeButton.setEnabled(true);
                                          } else {
                                              removeButton.setEnabled(false);
                                          }
                                      }
                                  }
        );
        addButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                grid.getContainerDataSource().addItem(new SchemaElement());

                //Issue when adding new items in non-empty grid. Last column was shifted down.
                grid.recalculateColumnWidths();
            }
        });
        removeButton.addClickListener(new Button.ClickListener() {
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
        final BeanItemContainer<SchemaElement> container = new BeanItemContainer<>(SchemaElement.class, new LinkedList<SchemaElement>());
        grid.setContainerDataSource(container);
        grid.setColumnOrder("type", "name", "description", "data");
        grid.setEditorEnabled(true);
        grid.addItemClickListener(new SchemaElementClickListener());
        for (Grid.Column c : grid.getColumns()) {
            c.setSortable(false);
        }
        grid.setSizeFull();
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
                Label release = new Label("Release: " + task.getFolder().getVersion());
                Label descr = new Label("Description: " + task.getDescription());

                container.addComponent(name);
                container.addComponent(release);
                container.addComponent(descr);
            }*/
        }
    }
}
