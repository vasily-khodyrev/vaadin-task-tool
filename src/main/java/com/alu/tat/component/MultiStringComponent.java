package com.alu.tat.component;

import com.alu.tat.entity.schema.SchemaElement;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by imalolet on 17.05.2016.
 */
public class MultiStringComponent extends CustomField<MultiStringBean> {

    private VerticalLayout main;
    private TextField multi;
    private String header;

    private SchemaElement element;

    /**
     * Constructor needed for testing purposes only.
     */
    public MultiStringComponent() {
        element = new SchemaElement("test_ms", "test_desc", SchemaElement.ElemType.MULTI_STRING, 0);
    }

    public MultiStringComponent(SchemaElement element) {
        this.header = element.getName();
        this.element = element;
    }

    @Override
    public Class getType() {
        return List.class;
    }

    @Override
    protected Component initContent() {
        main = new VerticalLayout();
        baseInit();

        main.setImmediate(true);
        main.addComponent(addField(""));

        return main;
    }


    @Override
    public MultiStringBean getValue() {
        LinkedHashMap<String, Integer> value = new LinkedHashMap();
        Integer m = Integer.parseInt(multi.getValue());
        //skip separator and label
        for (int i = 2; i < main.getComponentCount(); i++) {
            String v = getFieldValue(main.getComponent(i));
            if (v != null) {
                value.put(v, m);
            }
        }

        return new MultiStringBean(m, value);
    }

    @Override
    public void setValue(MultiStringBean bean) throws ReadOnlyException, Converter.ConversionException {
        main.removeAllComponents();

        baseInit();
        LinkedHashMap<String, Integer> value = bean.getValues();

        if (value != null && !value.isEmpty()) {
            for (Map.Entry<String, Integer> v : value.entrySet()) {
                main.addComponent(addField(v.getKey()));
            }
        } else {
            main.addComponent(addField(""));
        }
    }

    private void baseInit() {
        HorizontalLayout hl = new HorizontalLayout();
        Label label = new Label(header);
        hl.addComponent(label);
        multi = new TextField();
        multi.setValue(element.getMultiplier().toString());
        multi.setConverter(Integer.class);
        multi.addValidator(new IntegerRangeValidator("Estimate should be between 1 and 80", 1, 80));
        multi.setWidth(3f, Unit.EM);
        hl.addComponent(new HSeparator(20));
        hl.addComponent(multi);
        hl.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(multi, Alignment.MIDDLE_LEFT);
        main.addComponent(hl);
        main.addComponent(new VSeparator(20));
    }

    private GridLayout addField(String value) {
        final TextArea text = new TextArea("", value);
        text.setWordwrap(false);
        text.setCaption("Case description");
        text.setWidth("600px");

        final Button addBtn = new Button("", FontAwesome.PLUS);
        final Button removeBtn = new Button("", FontAwesome.MINUS);

        final HorizontalLayout buttonLayout = new HorizontalLayout(addBtn, removeBtn);
        buttonLayout.setSpacing(true);
        buttonLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);

        final GridLayout gridLayout = new GridLayout(1, 4, text, new VSeparator(20), buttonLayout, new VSeparator(50));

        addBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final int index = main.getComponentIndex(gridLayout);
                main.addComponent(addField(""), index + 1);
            }
        });

        removeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                main.removeComponent(gridLayout);
            }
        });

        return gridLayout;

    }

    private String getFieldValue(Component c) {
        GridLayout fieldLayout = ((GridLayout) c);
        TextArea text = (TextArea) fieldLayout.getComponent(0, 0);
        return text.getValue();
    }

}
