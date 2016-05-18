package com.alu.tat.util;

import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.vaadin.data.Property;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.util.*;

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
                    case MULTI_ENUM: {
                        LinkedList<String> items = (LinkedList<String>) value;
                        estim += se.getMultiplier();
                        StringBuilder sb = new StringBuilder();
                        for (String item : items) {
                            sb.append("," + item);
                        }
                        result.append("<b>" + se.getName() + ":</b> " + sb.toString() + "  - " + se.getMultiplier() + "m/d");
                        break;
                    }
                    case MULTI_STRING: {
                        LinkedList<String> items = (LinkedList<String>) value;
                        estim += se.getMultiplier() * items.size();
                        result.append("<b>" + se.getName() + ":</b> Estimate: " + se.getMultiplier() * items.size() + "m/d <br>");
                        int i = 1;
                        for (String item : items) {
                            result.append("<b> Option " + i + ":</b> " + item + "<br>");
                            i++;
                        }
                        break;
                    }
                    case DOMAIN:
                        continue;
                    default:
                        break;
                }

                switch (se.getType()) {
                    case MULTI_STRING:
                        break;
                    case MULTI_ENUM:
                        break;
                    default: {
                        result.append("<b>" + se.getName() + ":</b> " + value.toString() + "  - " + se.getMultiplier() + "m/d");
                        break;
                    }
                }
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
                case MULTI_ENUM: {
                    Object value = fieldMap.get(se.getName()).getValue();
                    Collection<String> items = (Collection<String>) value;
                    JSONArray ja = new JSONArray();
                    for (String item : items) {
                        ja.add(item);
                    }
                    json.put(se.getName(), ja);
                    break;
                }
                case MULTI_STRING: {
                    List<String> items = (List) fieldMap.get(se.getName()).getValue();
                    if (items != null) {
                        JSONArray ja = new JSONArray();
                        for (String item : items) {
                            ja.add(item);
                        }
                        json.put(se.getName(), ja);
                    }
                    break;
                }
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
                        case MULTI_ENUM: {
                            JSONArray ja = jso.getJSONArray(se.getName());
                            LinkedList<String> items = new LinkedList<>();
                            for (int i = 0; i < ja.size(); i++) {
                                String v = ja.getString(i);
                                items.add(v);
                            }
                            result.put(se.getName(), items);
                            break;
                        }
                        case MULTI_STRING: {
                            JSONArray ja = jso.getJSONArray(se.getName());
                            LinkedList<String> items = new LinkedList<>();
                            for (int i = 0; i < ja.size(); i++) {
                                String v = ja.getString(i);
                                items.add(v);
                            }
                            result.put(se.getName(), items);
                            break;
                        }
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
