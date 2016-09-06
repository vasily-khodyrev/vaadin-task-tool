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
    private String header;

    private volatile int visualFieldCount = 0;

    private volatile int initialFieldCount = 0;

    private SchemaElement element;

    private MultiStringBean originalState;

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
        main.addComponent(addField("", element.getMultiplier()));
        storeState();

        return main;
    }

    private void storeState() {
        originalState = getValue();
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
        //skip separator and label
        for (int i = 2; i < main.getComponentCount(); i++) {
            List<AbstractField> lv = getFieldValue(main.getComponent(i));
            if (lv.get(0).getValue() != null) {
                Integer m = 0;
                try {
                    m = Integer.parseInt((String) lv.get(1).getValue());
                } catch (Exception e) {
                    //keep 0
                }
                value.put((String) lv.get(0).getValue(), m);
            }
        }

        return new MultiStringBean(value);
    }

    @Override
    public void setValue(MultiStringBean bean) throws ReadOnlyException, Converter.ConversionException {
        main.removeAllComponents();

        baseInit();
        LinkedHashMap<String, Integer> value = bean.getValues();
        if (value != null && !value.isEmpty()) {
            for (Map.Entry<String, Integer> v : value.entrySet()) {
                main.addComponent(addField(v.getKey(), v.getValue()));
            }
        } else {
            main.addComponent(addField("", element.getMultiplier()));
        }
        storeState();
    }

    private void baseInit() {
        visualFieldCount = 0;
        HorizontalLayout hl = new HorizontalLayout();
        Label label = new Label(header);
        hl.addComponent(label);
        hl.addComponent(new HSeparator(20));
        hl.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        main.addComponent(hl);
        main.addComponent(new VSeparator(20));
    }

    private GridLayout addField(String value, Integer m) {
        incVisualFieldCount();

        final TextArea text = new TextArea("", value);
        text.addStyleName("v-textarea-normal");
        text.setWordwrap(false);
        text.setCaption("Case description");
        text.setWidth("600px");
        text.setRows(calcRowNum(value));

        TextField cmulti = new TextField();
        cmulti.setDescription("Estimate per case");
        cmulti.setValue(m.toString());
        cmulti.setConverter(Integer.class);
        cmulti.addValidator(new IntegerRangeValidator("Estimate should be between 1 and 160", 1, 160));
        cmulti.setWidth(3f, Unit.EM);

        final Button addBtn = new Button("", FontAwesome.PLUS);
        final Button removeBtn = new Button("", FontAwesome.MINUS);

        final HorizontalLayout buttonLayout = new HorizontalLayout(addBtn, removeBtn);
        buttonLayout.setSpacing(true);
        buttonLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);

        final GridLayout gridLayout = new GridLayout(2, 3, cmulti, text, new VSeparator(20), new VSeparator(20), new VSeparator(50), buttonLayout);

        addBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final int index = main.getComponentIndex(gridLayout);
                main.addComponent(addField("", element.getMultiplier()), index + 1);
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

    private List<AbstractField> getFieldValue(Component c) {
        GridLayout fieldLayout = ((GridLayout) c);
        TextField cmulti = (TextField) fieldLayout.getComponent(0, 0);
        TextArea text = (TextArea) fieldLayout.getComponent(1, 0);
        Integer mint = 0;
        try {
            mint = Integer.parseInt(cmulti.getValue());
        } catch (Exception e) {
            // wrong value - leave 0
        }
        List<AbstractField> result = new ArrayList<>(2);
        result.add(text);
        result.add(cmulti);
        return result;
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
        for (int i = 2; i < main.getComponentCount(); i++) {
            List<AbstractField> f = getFieldValue(main.getComponent(i));
            for (AbstractField af : f) {
                af.setEnabled(!readOnly);
            }
        }
    }

}
