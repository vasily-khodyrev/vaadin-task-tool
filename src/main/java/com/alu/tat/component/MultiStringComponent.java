package com.alu.tat.component;

import com.alu.tat.entity.schema.SchemaElement;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

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

    private volatile int visualFieldCount = 0;

    private volatile int initialFieldCount = 0;

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
        storeState();

        return main;
    }

    private void storeState() {
        initialFieldCount = visualFieldCount;
    }

    private boolean isFieldCountModified() {
        return initialFieldCount != visualFieldCount;
    }

    protected int getVisualFieldCount() {
        return visualFieldCount;
    }

    protected void incVisualFieldCount() {
        visualFieldCount++;
    }

    protected void decVisualFieldCount() {
        visualFieldCount--;
    }


    @Override
    public MultiStringBean getValue() {
        LinkedHashMap<String, Integer> value = new LinkedHashMap();
        Integer m = Integer.parseInt(multi.getValue());
        //skip separator and label
        for (int i = 2; i < main.getComponentCount(); i++) {
            String v = getFieldValue(main.getComponent(i)).getValue();
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
        storeState();
    }

    private void baseInit() {
        visualFieldCount = 0;

        HorizontalLayout hl = new HorizontalLayout();
        Label label = new Label(header);
        multi = new TextField();
        multi.setDescription("Estimate per case");
        multi.setValue(element.getMultiplier().toString());
        multi.setConverter(Integer.class);
        multi.addValidator(new IntegerRangeValidator("Estimate should be between 1 and 80", 1, 80));
        multi.setWidth(3f, Unit.EM);
        hl.addComponent(label);
        hl.addComponent(new HSeparator(20));
        hl.addComponent(multi);
        hl.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(multi, Alignment.MIDDLE_LEFT);
        main.addComponent(hl);
        main.addComponent(new VSeparator(20));
    }

    private GridLayout addField(String value) {
        incVisualFieldCount();

        final TextArea text = new TextArea("", value);
        text.addStyleName("v-textarea-normal");
        text.setWordwrap(false);
        text.setCaption("Case description");
        text.setWidth("600px");
        text.setRows(calcRowNum(value));

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
                if (getVisualFieldCount() > 1) {
                    main.removeComponent(gridLayout);
                    decVisualFieldCount();
                }
            }
        });

        return gridLayout;

    }

    private int calcRowNum(String s) {
        String cut = s.replace("\n", "");
        int k = s.length() - cut.length() + 3;
        return k;
    }

    private AbstractField<String> getFieldValue(Component c) {
        GridLayout fieldLayout = ((GridLayout) c);
        TextArea text = (TextArea) fieldLayout.getComponent(0, 0);
        return text;
    }

    @Override
    public boolean isModified() {
        boolean res = super.isModified();
        if (!res) {
            if (main != null) {
                if (isFieldCountModified()) {
                    return true;
                }
                for (int i = 2; i < main.getComponentCount(); i++) {
                    AbstractField<String> v = getFieldValue(main.getComponent(i));
                    if (v.isModified()) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        multi.setEnabled(!readOnly);
        for (int i = 2; i < main.getComponentCount(); i++) {
            AbstractField f = getFieldValue(main.getComponent(i));
            f.setEnabled(!readOnly);
        }
    }

}
