package com.alu.tat.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 14.07.15
 */
public abstract class AbstractActionView extends VerticalLayout implements View {

    protected boolean isCreate(String params) {
        return params != null && params.startsWith(UIConstants.OP_CREATE);
    }

    protected Long getUpdateId(String params) {
        Long result = null;
        if (params != null && params.startsWith(UIConstants.OP_UPDATE)) {
            String si = params.substring(UIConstants.OP_UPDATE.length() + 1);
            try {
                result = Long.valueOf(si);
            } catch (NumberFormatException e) {
                // bad luck
            }
        }
        return result;

    }

    protected Long getShowId(String params) {
        Long result = null;
        if (params != null && params.startsWith(UIConstants.OP_SHOW)) {
            String si = params.substring(UIConstants.OP_SHOW.length() + 1);
            try {
                result = Long.valueOf(si);
            } catch (NumberFormatException e) {
                // bad luck
            }
        }
        return result;
    }
}
