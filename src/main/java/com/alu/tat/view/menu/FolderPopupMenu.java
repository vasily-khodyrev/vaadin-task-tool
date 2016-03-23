package com.alu.tat.view.menu;

import com.alu.tat.entity.Folder;
import com.alu.tat.service.FolderService;
import com.alu.tat.view.UIConstants;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/18/2016
 */
public class FolderPopupMenu extends VerticalLayout implements PopupMenuManager.PopupContent {
    private Folder item;
    private Window window;

    public FolderPopupMenu(Folder item) {
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
                getUI().getCurrent().getNavigator().navigateTo(UIConstants.FOLDER_CREATE);
                closeWindow();
            }
        });

        Button updateFolder = new Button("Update");
        updateFolder.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getUI().getCurrent().getNavigator().navigateTo(UIConstants.FOLDER_CREATE);
                closeWindow();
            }
        });

        Button deleteFolder = new Button("Delete");
        deleteFolder.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (item != null) {
                    FolderService.removeFolder(item.getId());
                }
                closeWindow();
            }
        });
        if (item == null) {
            deleteFolder.setVisible(false);
        }
        layout.addComponents(createFolder, updateFolder, deleteFolder);
    }
}
