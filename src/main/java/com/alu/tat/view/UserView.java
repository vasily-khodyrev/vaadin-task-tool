package com.alu.tat.view;

import com.alu.tat.entity.User;
import com.alu.tat.service.UserService;
import com.alu.tat.util.PasswordTools;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import org.apache.commons.lang.StringUtils;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/16/2016
 */
public class UserView extends AbstractActionView {

    private Navigator navigator;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final boolean isCreate = isCreate(event.getParameters());
        final Long updateId = getUpdateId(event.getParameters());

        navigator = getUI().getNavigator();

        //Left section begin
        FormLayout form = new FormLayout();
        final TextField userLogin = new TextField("User login");
        if (!isCreate) {
            userLogin.setEnabled(false);
        }
        final TextField userName = new TextField("User Full Name");
        final PasswordField userPassword = new PasswordField("User Password");
        userPassword.addValidator(new PasswordTools.PasswordValidator(!isCreate));

        form.addComponent(userLogin);
        form.addComponent(userName);
        form.addComponent(userPassword);


        Button create = new Button(isCreate ? "Create" : "Update", new ThemeResource(("../runo/icons/16/ok.png")));
        Button back = new Button("Back", new ThemeResource(("../runo/icons/16/cancel.png")));

        HorizontalLayout buttonGroup = new HorizontalLayout(create, back);
        form.addComponent(buttonGroup);
        addComponent(form);

        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!userPassword.isValid()) {
                    Notification.show("Form data is not valid!",Notification.Type.ERROR_MESSAGE);
                    return;
                }
                User user;
                if (isCreate) {
                    user = new User();
                } else {
                    user = UserService.getUserById(updateId);
                }
                user.setName(userName.getValue());
                user.setLogin(userLogin.getValue());
                if (!StringUtils.isBlank(userPassword.getValue())) {
                    user.setPasswordHash(PasswordTools.getPwdHash(userPassword.getValue()));
                }
                if (!isCreate) {
                    UserService.updateUser(user);
                } else {
                    UserService.createUser(user);
                }

                navigator.navigateTo(UIConstants.VIEW_MAIN);
                Notification.show("User '" + user.getName() + "' is successfully " + (isCreate ? "created" : "updated"), Notification.Type.TRAY_NOTIFICATION);
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
            User user = UserService.getUserById(updateId);
            userName.setValue(user.getName());
            userLogin.setValue(user.getLogin());
        }

    }

}
