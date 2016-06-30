package com.alu.tat.view.menu;

import com.alu.tat.entity.User;
import com.alu.tat.service.UserService;
import com.alu.tat.util.SessionHelper;
import com.alu.tat.view.UIConstants;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by
 * User: vkhodyre
 * Date: 5/19/2016
 */
public class UserPopupMenu extends VerticalLayout implements PopupMenuManager.PopupContent {
    private User item;
    private Window window;
    private VaadinSession session;

    public UserPopupMenu(User item, VaadinSession session) {
        super();
        this.item = item;
        this.session = session;
        initButtons(this);

    }

    @Override
    public String getTitle() {
        return "User";
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
        createFolder.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        createFolder.addStyleName("accordianButton");
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
        updateFolder.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        updateFolder.addStyleName("accordianButton");
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
                        Notification.show("Failed to remove User '" + item.getName() + "' due to " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
                    }
                    getUI().getCurrent().getNavigator().navigateTo(UIConstants.VIEW_MAIN);
                }
            }
        });
        deleteFolder.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        deleteFolder.addStyleName("accordianButton");

        if (item == null) {
            updateFolder.setVisible(false);
            deleteFolder.setVisible(false);
        } else {
            if (!SessionHelper.getCurrentUser(session).getIsSystem()) {
                createFolder.setVisible(false);
                deleteFolder.setVisible(false);
                if (!item.equals(SessionHelper.getCurrentUser(session))) {
                    layout.addComponent(new Label("No actions allowed"));
                }
            }
        }
        layout.addComponents(createFolder, updateFolder, deleteFolder);
        layout.setComponentAlignment(createFolder, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(updateFolder, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(deleteFolder, Alignment.MIDDLE_CENTER);
        //layout.addStyleName("valo-menu-part");
    }
}