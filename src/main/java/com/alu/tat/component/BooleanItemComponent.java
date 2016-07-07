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

    private BooleanItemBean originalState;

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
        main = new GridLayout(4, 1);
        //main.setStyleName("tasktool");
        main.setSpacing(true);
        //main.setColumnExpandRatio(0, 0.2f);
        main.setColumnExpandRatio(1, 0.1f);
        main.setColumnExpandRatio(2, 0.1f);
        main.setColumnExpandRatio(3, 0.6f);
        main.setWidth("100%");
        Label cLabel = new Label(element.getName());
        cLabel.setSizeUndefined();//VAADIN hack for labels inside GridLayout

        value = new CheckBox();
        multi = new TextField();
        multi.setDescription("Estimate");
        //Label caption = new com.vaadin.ui.Label(header);
        multi.setValue(element.getMultiplier().toString());
        multi.setConverter(Integer.class);
        multi.addValidator(new IntegerRangeValidator("You can only put numbers between 0 and 80", 0, 80));
        multi.setWidth(3f, Unit.EM);
        multi.setEnabled(false);
        comment = new TextArea();
        comment.addStyleName("v-textarea-normal");
        comment.setWidth("600px");
        comment.setRows(calcRowNum(""));
        comment.setWordwrap(true);
        comment.setEnabled(false);
        comment.setHeightUndefined();
        value.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if ((Boolean) event.getProperty().getValue()) {
                    if (element.getMultiplier() > 0) {
                        multi.setEnabled(true);
                    }
                    comment.setRows(calcRowNum(comment.getValue()));
                    comment.setEnabled(true);
                } else {
                    multi.setValue(element.getMultiplier().toString());
                    multi.setEnabled(false);
                    comment.setRows(calcRowNum(""));
                    comment.setEnabled(false);
                }
            }
        });
        main.addComponent(cLabel);
        main.addComponent(value);
        //main.addComponent(new HSeparator(20));
        main.addComponent(multi);
        //main.addComponent(new HSeparator(20));
        main.addComponent(comment);
        main.setComponentAlignment(cLabel, Alignment.MIDDLE_CENTER);
        main.setComponentAlignment(value, Alignment.MIDDLE_CENTER);
        main.setComponentAlignment(multi, Alignment.MIDDLE_CENTER);
        main.setComponentAlignment(comment, Alignment.MIDDLE_CENTER);
        main.setImmediate(true);

        storeOriginalState();
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
        comment.setRows(calcRowNum(c));

        storeOriginalState();
    }

    private int calcRowNum(String s) {
        String cut = s.replace("\n", "");
        int k = s.length() - cut.length() + 3;
        return k;
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
        if (value != null) {
            value.validate();
        }
        if (multi != null) {
            multi.validate();
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
        multi.setEnabled(!readOnly);
        comment.setEnabled(!readOnly);
    }
}
