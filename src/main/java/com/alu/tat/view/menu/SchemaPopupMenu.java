package com.alu.tat.view.menu;

import com.alu.tat.entity.schema.Schema;
import com.alu.tat.service.SchemaService;
import com.alu.tat.view.UIConstants;
import com.vaadin.ui.*;

/**
 * Created by
 * User: vkhodyre
 * Date: 5/19/2016
 */
public class SchemaPopupMenu extends VerticalLayout implements PopupMenuManager.PopupContent {
    private Schema item;
    private Window window;

    public SchemaPopupMenu(Schema item) {
        super();
        this.item = item;
        initButtons(this);
    }

    @Override
    public void setWindow(Window w) {
        this.window = w;
    }

    private void closeWindow() {
        if (window != null) {
            window.close();
        }
    }

    private void initButtons(VerticalLayout layout) {
        Button createFolder = new Button("Create");
        createFolder.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getUI().getCurrent().getNavigator().navigateTo(UIConstants.SCHEMA_CREATE);
                closeWindow();
            }
        });

        Button updateFolder = new Button("Update");
        updateFolder.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (item != null) {
                    getUI().getCurrent().getNavigator().navigateTo(UIConstants.SCHEMA_UPDATE + item.getId());
                }
                closeWindow();
            }
        });

        Button deleteFolder = new Button("Delete");
        deleteFolder.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                closeWindow();
                if (item != null) {
                    String schemaName = item.getName();
                    try {
                        SchemaService.removeSchema(item.getId());
                        Notification.show("Schema '" + schemaName + "' successfully removed.", Notification.Type.TRAY_NOTIFICATION);
                    } catch (Exception e) {
                        Notification.show("Failed to remove Schema '" + item.getName() + "' due to " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
                    }
                    getUI().getCurrent().getNavigator().navigateTo(UIConstants.VIEW_MAIN);
                }
            }
        });
        if (item == null) {
            updateFolder.setVisible(false);
            deleteFolder.setVisible(false);
        }
        layout.addComponents(createFolder, updateFolder, deleteFolder);
        layout.setComponentAlignment(createFolder, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(updateFolder, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(deleteFolder, Alignment.MIDDLE_CENTER);
    }
}
