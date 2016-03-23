package com.alu.tat.util;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/22/2015
 */
public class UIComponentFactory {
    /**
     * Single factory method for generation UI components with setting id's for testing purpose.
     *
     * @param clas    Class extending {@link com.vaadin.ui.Component}
     * @param caption Caption of the component
     * @param testId  Test ID for the component
     * @param <T>
     * @return {@link Component} instance with caption and test id configured.
     */
    public static <T extends Component> T getComponent(Class<T> clas, String caption, String testId) {
        T component = null;
        try {
            component = clas.newInstance();
            component.setCaption(caption);
            component.setId(testId);
        } catch (Exception e) {
            // Epic fail
        }
        return component;
    }

    /**
     * Single factory method for generation UI components with setting id's for testing purpose.
     *
     * @param clas    Class extending {@link com.vaadin.ui.Component}
     * @param caption Caption of the component
     * @param testId  Test ID for the component
     * @param icon    Resource icon
     * @param <T>
     * @return {@link Component} instance with caption and test id configured.
     */
    public static <T extends Component> T getComponent(Class<T> clas, String caption, String testId, Resource icon) {
        T component = getComponent(clas, caption, testId);
        if (component != null) {
            component.setIcon(icon);
        }
        return component;
    }

    /**
     * Factory method for generating Button instances with listeners
     *
     * @param caption  Caption of the component
     * @param testId   Test ID for the component
     * @param listener Button click listener
     * @return {@link Button} Button instance with caption and test id configured and Listener configured.
     */
    public static Button getButton(String caption, String testId, Button.ClickListener listener) {
        Button component = getComponent(Button.class, caption, testId);
        if (component != null) {
            component.addClickListener(listener);
        }
        return component;
    }

    /**
     * Factory method for generating Button instances with icon
     *
     * @param caption  Caption of the component
     * @param testId   Test ID for the component
     * @param icon Resource icon
     * @return {@link Button} Button instance with caption and test id configured and icon set.
     */
    public static Button getButton(String caption, String testId, Resource icon) {
        return getComponent(Button.class, caption, testId, icon);
    }

    /**
     * Factory method for generating Button instances
     *
     * @param caption  Caption of the component
     * @param testId   Test ID for the component
     * @return {@link Button} Button instance with caption and test id configured and Listener configured.
     */
    public static Button getButton(String caption, String testId) {
        return getComponent(Button.class, caption, testId);
    }
}
