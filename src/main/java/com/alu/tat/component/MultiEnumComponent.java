package com.alu.tat.component;

import com.alu.tat.entity.schema.SchemaElement;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 23.06.2016
 */
public class MultiEnumComponent extends CustomField<MultiEnumBean> {
    private HorizontalLayout main;
    private ListSelect value;

    private SchemaElement element;
    private MultiEnumBean originalState;

    /**
     * Constructor needed for testing purposes only.
     */
    public MultiEnumComponent() {
        element = new SchemaElement("test_menum", "test_desc", SchemaElement.ElemType.MULTI_ENUM, 0);
    }

    public MultiEnumComponent(SchemaElement element) {
        this.element = element;
    }

    @Override
    protected Component initContent() {
        main = new HorizontalLayout();
        Label label = new Label(element.getName());
        value = new ListSelect();
        if (element.getType() == SchemaElement.ElemType.MULTI_ENUM) {
            value.setMultiSelect(true);
        }
        String data = element.getData();
        String[] options = data.split(";");
        List<String> allOptions = new LinkedList<>();
        List<String> defaultOptions = new LinkedList<>();
        String singleDefOption = null;
        for (String s : options) {
            String option;
            if (s.startsWith("*")) {
                option = s.substring(1);
                defaultOptions.add(option);
                singleDefOption = option;
            } else {
                option = s;
            }
            allOptions.add(option);
        }
        value.setRows(allOptions.size());
        value.addItems(allOptions);
        if (element.getType() == SchemaElement.ElemType.MULTI_ENUM) {
            value.setValue(defaultOptions);
        } else {
            if (singleDefOption != null) {
                value.setValue(singleDefOption);
            }
        }
        main.addComponents(label, new HSeparator(20), value);

        storeOriginalState();

        return main;
    }

    @Override
    public Class<? extends MultiEnumBean> getType() {
        return MultiEnumBean.class;
    }

    @Override
    public void setValue(MultiEnumBean newFieldValue) throws ReadOnlyException, Converter.ConversionException {
        List<String> b = newFieldValue.getValue();
        value.setValue(b);

        storeOriginalState();
    }

    @Override
    public MultiEnumBean getValue() throws ReadOnlyException, Converter.ConversionException {
        Object o = value.getValue();
        if (o instanceof Collection) {
            List<String> b = new LinkedList<>((Collection<String>) value.getValue());
            return new MultiEnumBean(b);
        } else {
            List<String> l = new LinkedList<>();
            l.add((String) o);
            return new MultiEnumBean(l);
        }
    }

    private void storeOriginalState() {
        originalState = getValue();
    }

    private boolean isStateChanged() {
        return originalState != null && !originalState.equals(getValue());
    }

    @Override
    public boolean isModified() {
        return super.isModified() || isStateChanged();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        value.setEnabled(!readOnly);
    }
}
