package com.alu.tat.component;

import com.alu.tat.entity.schema.SchemaElement;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 23.06.2016
 */
public class TaskComponentFactory {

    public static AbstractField getField(SchemaElement se) {
        final AbstractField c;
        switch (se.getType()) {
            case BOOLEAN: {
                c = new BooleanItemComponent(se);
                break;
            }
            case MULTI_ENUM: {
                ListSelect cb = new ListSelect(se.getName());
                cb.setMultiSelect(true);
                String data = se.getData();
                String[] options = data.split(";");
                cb.setRows(options.length);
                cb.addItems(options);
                c = cb;
                break;
            }
            case MULTI_STRING: {
                c = new MultiStringComponent(se);
                break;
            }
            case INTEGER: {
                c = new TextField(se.getName());
                c.setConverter(Integer.class);
                break;
            }
            case STRING: {
                c = new TextArea(se.getName());
                break;
            }
            case DOMAIN: {
                return null;
            }
            default: {
                c = new TextField();
                break;
            }
        }
        c.setDescription(se.getDescription());
        return c;
    }
}
