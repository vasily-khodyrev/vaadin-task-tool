package com.alu.tat.view;

import com.alu.tat.entity.User;
import com.alu.tat.service.UserService;
import com.alu.tat.util.PasswordTools;
import com.alu.tat.util.SessionHelper;
import com.alu.tat.util.UIComponentFactory;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by
 * User: vkhodyre
 * Date: 2/25/2016
 */
public class LoginView extends CustomComponent implements View,
        Button.ClickListener {

    private final static Logger logger =
            LoggerFactory.getLogger(LoginView.class);

    public static final String NAME = "login";

    private final TextField user;

    private final PasswordField password;

    private final Button loginButton;

    public LoginView() {
        setSizeFull();

        // Create the user input field
        user = new TextField("User:");
        user.setWidth("300px");
        user.setRequired(true);
        user.setInputPrompt("Your login");
        user.addValidator(new StringLengthValidator(
                "User login length must not be between 1 and 64 symbols", 1, 64, false));
        user.setInvalidAllowed(false);

        // Create the password input field
        password = new PasswordField("Password:");
        password.setWidth("300px");
        password.addValidator(new PasswordTools.PasswordValidator(false));
        password.setRequired(true);
        password.setValue("");
        password.setNullRepresentation("");

        // Create login button
        loginButton = UIComponentFactory.getButton("Login", "LOGINVIEW_LOGIN_BUTTON", this);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

        // Add both to a panel
        VerticalLayout fields = new VerticalLayout(user, password, loginButton);
        fields.setCaption("Please login to access the application.");
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();

        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // focus the username field when user arrives to the login view
        user.focus();
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        //
        // Validate the fields using the navigator. By using validors for the
        // fields we reduce the amount of queries we have to use to the database
        // for wrongly entered passwords
        //
        if (!user.isValid() || !password.isValid()) {
            return;
        }

        String username = user.getValue();
        String password = this.password.getValue();
        logger.trace("Username " + username);
        logger.trace("Password = " + password);
        //
        // Validate username and password with database here. For examples sake
        // I use a dummy username and password.
        //
        User u = UserService.getUser(username);
        String pwdHash = null;
        if (u != null) {
            pwdHash = PasswordTools.getPwdHash(password);
        } else {
            logger.trace("User with username " + username + " not found");
        }
        boolean isValid = u != null && pwdHash != null && pwdHash.equals(u.getPasswordHash());

        if (isValid) {
            logger.debug("User " + username + " is authorized - password is valid");
            // Store the current user in the service session
            SessionHelper.setCurrentUser(getSession(),u);

            // Navigate to main view
            getUI().getNavigator().navigateTo(UIConstants.VIEW_MAIN);//

        } else {
            logger.debug("User " + username + " is not authorized - password is invalid");
            // Wrong password clear the password field and refocuses it
            this.password.setValue(null);
            this.password.focus();

        }
    }
}