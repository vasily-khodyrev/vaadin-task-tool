package com.alu.tat.component;

import com.alu.tat.entity.schema.SchemaElement;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.*;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 19.06.2016
 */
public class BooleanItemComponent extends CustomField<BooleanItemBean> {
    private GridLayout main;
    private CheckBox value;
    private TextField multi;
    private TextArea comment;

    private SchemaElement element;

    /**
     * Constructor needed for testing purposes only.
     */
    public BooleanItemComponent() {
        element = new SchemaElement("test_bool", "test_desc", SchemaElement.ElemType.BOOLEAN, 0);
    }

    public BooleanItemComponent(SchemaElement element) {
        this.element = element;
    }

    @Override
    protected Component initContent() {
        main = new GridLayout(3, 1);
        main.setColumnExpandRatio(1, 4);
        main.setColumnExpandRatio(2, 1);
        main.setColumnExpandRatio(3, 4);
        main.setWidth("100%");
        value = new CheckBox(element.getName());
        multi = new TextField();
        //Label caption = new com.vaadin.ui.Label(header);
        multi.setValue(element.getMultiplier().toString());
        multi.setConverter(Integer.class);
        multi.addValidator(new IntegerRangeValidator("You can only put numbers between 0 and 80", 0, 80));
        multi.setWidth(3f, Unit.EM);
        multi.setEnabled(false);
        comment = new TextArea();
        comment.setWidth("600px");
        comment.setWordwrap(false);
        comment.setEnabled(false);
        value.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if ((Boolean) event.getProperty().getValue()) {
                    multi.setEnabled(true);
                    comment.setEnabled(true);
                } else {
                    multi.setValue(element.getMultiplier().toString());
                    multi.setEnabled(false);
                    comment.setValue("");
                    comment.setEnabled(false);
                }
            }
        });

        main.addComponent(value);
        //main.addComponent(new HSeparator(20));
        main.addComponent(multi);
        //main.addComponent(new HSeparator(20));
        main.addComponent(comment);
        main.setComponentAlignment(value, Alignment.MIDDLE_CENTER);
        main.setComponentAlignment(multi, Alignment.MIDDLE_CENTER);
        main.setComponentAlignment(comment, Alignment.MIDDLE_CENTER);
        main.setImmediate(true);
        return main;
    }

    @Override
    public Class getType() {
        return BooleanItemBean.class;
    }

    @Override
    public void setValue(BooleanItemBean newFieldValue) throws ReadOnlyException, Converter.ConversionException {
        Boolean b = newFieldValue.getValue();
        Integer i = newFieldValue.getMulti();
        String c = newFieldValue.getComments();
        value.setValue(b);
        multi.setValue(i.toString());
        comment.setValue(c);
    }

    @Override
    public BooleanItemBean getValue() throws ReadOnlyException, Converter.ConversionException {
        Boolean b = value.getValue();
        Integer i = Integer.parseInt(multi.getValue());
        String c = comment.getValue();
        return new BooleanItemBean(b, i, c);
    }

    @Override
    public void validate() throws Validator.InvalidValueException {
        super.validate();
        value.validate();
        multi.validate();
    }
}
