package com.alu.tat.component;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imalolet on 17.05.2016.
 */
public class MultiStringComponent extends CustomField<List<String>> {

    private VerticalLayout main;
    private String header;

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
    public List<String> getValue() {
        List<String> value = new ArrayList();
        //skip separator and label
        for (int i = 2; i < main.getComponentCount(); i++) {
            String v = getFieldValue(main.getComponent(i));
            if (v != null) {
                value.add(v);
            }
        }
        return value;
    }

    @Override
    public void setValue(List<String> value) throws ReadOnlyException, Converter.ConversionException {
        main.removeAllComponents();

        baseInit();

        if (value != null && !value.isEmpty()) {
            for (String v : value) {
                main.addComponent(addField(v));
            }
        } else {
            main.addComponent(addField(""));
        }
    }

    private void baseInit() {
        main.addComponent(new Label(this.getHeader()));
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


    public String getHeader() {
        return header;
    }

    public void setHeader(String heading) {
        this.header = heading;
    }
}
