package com.alu.tat.component;

import com.vaadin.ui.Label;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 19.06.2016
 */
public class HSeparator  extends Label {

    public HSeparator(int height) {
        this.setWidth(height + "px");
        this.setHeight(null);
    }
}