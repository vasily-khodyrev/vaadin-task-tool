package com.alu.tat.view.menu;

import com.alu.tat.entity.Folder;
import com.alu.tat.entity.User;
import com.alu.tat.service.FolderService;
import com.alu.tat.service.UserService;
import com.alu.tat.view.UIConstants;
import com.vaadin.ui.*;

/**
 * Created by
 * User: vkhodyre
 * Date: 5/19/2016
 */
public class UserPopupMenu extends VerticalLayout implements PopupMenuManager.PopupContent {
    private User item;
    private Window window;

    public UserPopupMenu(User item) {
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
                getUI().getCurrent().getNavigator().navigateTo(UIConstants.USER_CREATE);
                closeWindow();
            }
        });

        Button updateFolder = new Button("Update");
        updateFolder.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (item != null) {
                    getUI().getCurrent().getNavigator().navigateTo(UIConstants.USER_UPDATE + item.getId());
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
                    try {
                        UserService.removeUser(item.getId());
                        Notification.show("User '" + item.getName() + "' successfully removed.", Notification.Type.TRAY_NOTIFICATION);
                    } catch (Exception e) {
                        Notification.show("Failed to remove User '" + item.getName() + "' due to " + e.getMessage() , Notification.Type.ERROR_MESSAGE);
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