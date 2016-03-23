package com.alu.tat.util;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.vaadin.data.Property;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/15/2015
 */
public class TaskPresenter {

    public static String getHtmlView(Task task) {
        StringBuilder result = new StringBuilder();
        result.append("<h1>Task: " + task.getName() + "</h1>");
        result.append("<br>");
        result.append("<b>Description:</b> " + task.getDescription());
        result.append("<br>");
        result.append("<b>Release:</b> " + task.getFolder());
        result.append("<br>");
        result.append("<b>Analyzer:</b> " + task.getAuthor());
        result.append("<br>");
        result.append("<br>");
        Schema schema = task.getSchema();
        Map<String, Object> valueMap = convertFromJSON(task.getData(), schema);
        int estim = 0;
        //TODO: Very strange behavior. If you get elemList through task.schema.elemList you'll get ALL elems from all tasks
        schema = SchemaService.getInstance().getSchema(schema.getId());
        List<SchemaElement> seList = schema.getElementsList();
        for (SchemaElement se : seList) {
            if (valueMap.containsKey(se.getName())) {
                Object value = valueMap.get(se.getName());
                switch (se.getType()) {
                    case BOOLEAN: {
                        if (!(Boolean) value) {
                            continue;
                        }
                        estim += se.getMultiplier();
                        break;
                    }
                    case INTEGER: {
                        Integer i = Integer.valueOf((String) value);
                        if (i == 0) {
                            continue;
                        }
                        estim += se.getMultiplier() * i;
                        break;
                    }
                    case STRING:
                        break;
                    case DOMAIN:
                        continue;
                    default:
                        break;
                }
                result.append("<b>" + se.getName() + ":</b> " + value.toString() + "  - " + se.getMultiplier() + "m/d");
                result.append("<br>");
            }
        }
        result.append("<br>");
        result.append("<b>Total Estimate:</b> " + estim + "m/d");
        return result.toString();
    }

    public static String convertToData(Schema schema, Map<String, Property> fieldMap) {
        JSONObject json = new JSONObject();
        for (SchemaElement se : schema.getElementsList()) {
            switch (se.getType()) {
                case DOMAIN:
                    break;
                case INTEGER:
                case STRING:
                case BOOLEAN:
                default: {
                    Object value = fieldMap.get(se.getName()).getValue();
                    json.put(se.getName(), value);
                    break;
                }
            }
        }
        return json.toString();
    }

    public static Map<String, Object> convertFromJSON(String data, Schema schema) {
        Map<String, Object> result = new HashMap<>();
        JSON json = JSONSerializer.toJSON(data);
        if (json instanceof JSONObject) {
            JSONObject jso = (JSONObject) json;
            for (SchemaElement se : schema.getElementsList()) {
                if (jso.has(se.getName())) {
                    switch (se.getType()) {
                        case DOMAIN:
                            break;
                        case INTEGER: {
                            String value = jso.getString(se.getName());
                            String o = value;
                            result.put(se.getName(), o);
                            break;
                        }
                        case STRING: {
                            String value = jso.getString(se.getName());
                            String o = value;
                            result.put(se.getName(), o);
                            break;
                        }
                        case BOOLEAN: {
                            String value = jso.getString(se.getName());
                            Boolean o = Boolean.valueOf(value);
                            result.put(se.getName(), o);
                            break;
                        }
                        default:
                            break;

                    }
                }

            }
        }
        return result;
    }
}
