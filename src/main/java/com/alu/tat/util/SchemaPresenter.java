package com.alu.tat.util;

import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;

import java.util.List;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/16/2015
 */
public class SchemaPresenter {
    public static String getHtmlView(Schema schema) {
        StringBuilder result = new StringBuilder();
        result.append("<h1>Schema: " + schema.getName());
        result.append("<br>");
        result.append("Description: " + schema.getDescription());
        result.append("</h1>");
        result.append("<br>");
        result.append("<br>");

        List<SchemaElement> seList = schema.getElementsList();
        for (SchemaElement se : seList) {
            switch (se.getType()) {
                case DOMAIN:
                    break;
                default: {
                    result.append("<b>" + se.getName() + ":</b> Type=" + se.getType() + " Multi=" + se.getMultiplier());
                    result.append("<br>");
                    break;
                }
            }
        }
        return result.toString();
    }
}
