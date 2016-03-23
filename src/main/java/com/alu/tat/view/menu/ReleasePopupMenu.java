package com.alu.tat.view.menu;

import com.alu.tat.entity.Task;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/18/2016
 */
public class ReleasePopupMenu extends VerticalLayout {
    private Task.Release item;

    public ReleasePopupMenu(Task.Release item) {
        super();
        this.item = item;
        //todo: finish
        this.addComponent(new Label(item.name()));
    }
}
