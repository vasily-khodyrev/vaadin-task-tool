package com.alu.tat.component;

import com.alu.tat.entity.schema.SchemaElement;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.*;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 19.06.2016
 */
public class BooleanItemComponent extends CustomField<Map.Entry<Boolean,Integer>> {
    private HorizontalLayout main;
    private CheckBox value;
    private TextField multi;

    private SchemaElement element;
    private String header;

    public BooleanItemComponent(String header, SchemaElement element) {
        this.header = header;
        this.element = element;
    }

    @Override
    protected Component initContent() {
        main = new HorizontalLayout();
        value = new CheckBox(header);
        multi = new TextField();
        //Label caption = new com.vaadin.ui.Label(header);
        multi.setValue(element.getMultiplier().toString());
        multi.setConverter(Integer.class);
        multi.addValidator(new IntegerRangeValidator("You can only put numbers between 0 and 80", 0, 80));
        multi.setWidth(3f, Unit.EM);
        multi.setEnabled(false);
        value.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if ((Boolean) event.getProperty().getValue()) {
                    multi.setEnabled(true);
                } else {
                    multi.setValue(element.getMultiplier().toString());
                    multi.setEnabled(false);
                }
            }
        });

        main.addComponent(value);
        main.addComponent(new HSeparator(20));
        main.addComponent(multi);
        main.setComponentAlignment(value,Alignment.MIDDLE_LEFT);
        main.setComponentAlignment(multi,Alignment.MIDDLE_LEFT);
        main.setImmediate(true);

        return main;
    }

    @Override
    public Class getType() {
        return Map.Entry.class;
    }

    @Override
    public void setValue(Map.Entry<Boolean, Integer> newFieldValue) throws ReadOnlyException, Converter.ConversionException {
        Boolean b = newFieldValue.getKey();
        Integer i = newFieldValue.getValue();
        value.setValue(b);
        multi.setValue(i.toString());
    }

    @Override
    public Map.Entry<Boolean, Integer> getValue() throws ReadOnlyException, Converter.ConversionException {
        Boolean b = value.getValue();
        Integer i = Integer.parseInt(multi.getValue());
        return new AbstractMap.SimpleEntry<Boolean, Integer>(b,i);
    }

    @Override
    public void validate() throws Validator.InvalidValueException {
        super.validate();
        value.validate();
        multi.validate();
    }
}
