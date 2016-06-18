package com.alu.tat.view;

import com.alu.tat.entity.Folder;
import com.alu.tat.entity.User;
import com.alu.tat.service.FolderService;
import com.alu.tat.service.UserService;
import com.alu.tat.util.UIComponentFactory;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;

import java.util.Arrays;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/23/2016
 */
public class FolderView extends AbstractActionView {

    private Navigator navigator;

    private final String NOT_SET = "NOT SET";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final boolean isCreate = isCreate(event.getParameters());
        final Long updateId = getUpdateId(event.getParameters());
        final Folder selectedFolder = (Folder) getUI().getSession().getAttribute("selectedFolder");
        navigator = getUI().getNavigator();

        //Left section begin
        FormLayout form = new FormLayout();
        final TextField folderName = new TextField("Folder name");
        folderName.addValidator(new StringLengthValidator(
                "Folder name must not be empty", 1, 255, false));
        final ComboBox rootFolder = new ComboBox("Parent Folder");
        for (Folder folder : FolderService.getFolders()) {
            rootFolder.addItem(folder);
        }
        if (selectedFolder != null) {
            rootFolder.select(selectedFolder);
        }
        //rootFolder.setEnabled(false);
        form.addComponent(folderName);
        form.addComponent(rootFolder);


        Button createOrUpdate = UIComponentFactory.getButton(isCreate ? "Create" : "Update", "FOLDERVIEW_CREATEORUPDATE_BUTTON", new ThemeResource(("../runo/icons/16/ok.png")));
        Button back = UIComponentFactory.getButton("Back", "FOLDERVIEW_CANCEL_BUTTON", new ThemeResource(("../runo/icons/16/cancel.png")));

        HorizontalLayout buttonGroup = new HorizontalLayout(createOrUpdate, back);
        form.addComponent(buttonGroup);
        addComponent(form);

        createOrUpdate.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Folder folder;
                if (isCreate) {
                    folder = new Folder();
                } else {
                    folder = FolderService.getFolderById(updateId);
                }
                folder.setName(folderName.getValue());
                Object rootObj = rootFolder.getValue();
                if (rootObj instanceof String) {
                    folder.setRoot(null);
                } else {
                    folder.setRoot((Folder) rootObj);
                }
                if (!isCreate) {
                    FolderService.updateFolder(folder);
                } else {
                    FolderService.createFolder(folder);
                }

                navigator.navigateTo(UIConstants.VIEW_MAIN);
                Notification.show("Folder '" + folder.getName() + "' is successfully " + (isCreate ? "created" : "updated"), Notification.Type.TRAY_NOTIFICATION);
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
            Folder folder = FolderService.getFolderById(updateId);
            folderName.setValue(folder.getName());
            if (folder.getRoot() != null) {
                rootFolder.select(folder.getRoot());
            }
        }

    }

}