package com.alu.tat.component;

import com.alu.tat.entity.schema.SchemaElement;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.Collections;
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
    //private TextField multi;
    //private TextArea comment;

    private SchemaElement element;

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
        value.setMultiSelect(true);
        String data = element.getData();
        String[] options = data.split(";");
        value.setRows(options.length);
        value.addItems(options);
        main.addComponents(label, new HSeparator(20), value);
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
    }

    @Override
    public MultiEnumBean getValue() throws ReadOnlyException, Converter.ConversionException {
        List<String> b = new LinkedList<String>((Collection<String>) value.getValue());
        return new MultiEnumBean(b);
    }
}
