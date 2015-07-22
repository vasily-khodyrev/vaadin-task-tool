package com.alu.tat.view.ui;

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
     * @param clas Class extending {@link com.vaadin.ui.Component}
     * @param caption Caption of the component
     * @param testId Test ID for the component
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
}
