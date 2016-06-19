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
        result.append("<h1>Task: " + putString(task.getName()) + "</h1>");
        result.append("<br>");
        result.append("<b>Description:</b> " + putString(task.getDescription()));
        result.append("<br>");
        result.append("<b>Folder:</b> " + putString(task.getFolder()));
        result.append("<br>");
        result.append("<b>Analyzer:</b> " + putString(task.getAuthor()));
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
                        Map.Entry<Boolean, Integer> bi = (Map.Entry) value;
                        if (!bi.getKey()) {
                            continue;
                        }
                        Integer multi = bi.getValue();
                        if (multi == null) {
                            multi = se.getMultiplier();
                        }
                        estim += multi;
                        result.append("<b>" + se.getName() + ":</b> " + "yes");
                        if (se.getMultiplier() > 0) {
                            result.append(" - " + getDaysPrint(multi));
                        }
                        break;
                    }
                    case INTEGER: {
                        Integer i = Integer.valueOf((String) value);
                        if (i == 0) {
                            continue;
                        }
                        estim += se.getMultiplier() * i;
                        result.append("<b>" + se.getName() + ":</b> " + i + " - " + getDaysPrint(se.getMultiplier() * i));
                        break;
                    }
                    case STRING:
                        result.append("<b>" + se.getName() + ":</b> " + putString(value));
                        break;
                    case MULTI_ENUM: {
                        LinkedList<String> items = (LinkedList<String>) value;
                        estim += se.getMultiplier();
                        StringBuilder sb = null;
                        for (String item : items) {
                            if (sb == null) {
                                sb = new StringBuilder(item);
                            } else {
                                sb.append("," + item);
                            }
                        }
                        String sbs = sb != null ? sb.toString() : "";
                        result.append("<b>" + se.getName() + ":</b> " + sbs);
                        if (se.getMultiplier() > 0) {
                            result.append(" - " + getDaysPrint(se.getMultiplier()));
                        }
                        break;
                    }
                    case MULTI_STRING: {
                        LinkedList<String> items = (LinkedList<String>) value;
                        estim += se.getMultiplier() * items.size();
                        result.append("<b>" + se.getName() + ":</b> " + getDaysPrint(se.getMultiplier() * items.size()) + "<br>");
                        int i = 1;
                        for (String item : items) {
                            result.append("<b> Option " + i + ":</b> " + putString(item) + "<br><br>");
                            i++;
                        }
                        break;
                    }
                    case DOMAIN:
                        continue;
                    default:
                        result.append("<b>" + se.getName() + ":</b> " + putString(value));
                        if (se.getMultiplier() > 0) {
                            result.append(" - " + getDaysPrint(se.getMultiplier()));
                        }
                        break;
                }
                result.append("<br>");
            }
        }
        result.append("<br>");
        result.append("<b>Total Estimate:</b> " + getDaysPrint(estim));
        return result.toString();
    }

    private static String putString(Object s) {
        if (s != null) {
            String st = s.toString();
            String cc = st.replace("\n", "<br/>");
            return cc;
        }
        return "N/A";
    }

    private static String getDaysPrint(Integer i) {
        String result = "";
        if (i > 0) {
            result = String.format("%.1f m/d", i / 8f);
        }
        return result;
    }

    public static String convertToData(Schema schema, Map<String, Property> fieldMap) {
        JSONObject json = new JSONObject();
        for (SchemaElement se : schema.getElementsList()) {
            JSONObject jo = new JSONObject();
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
                    jo.put("value", ja);
                    json.put(se.getName(), jo);
                    break;
                }
                case MULTI_STRING: {
                    List<String> items = (List) fieldMap.get(se.getName()).getValue();
                    if (items != null) {
                        JSONArray ja = new JSONArray();
                        for (String item : items) {
                            ja.add(item);
                        }
                        jo.put("value", ja);
                        json.put(se.getName(), jo);
                    }
                    break;
                }
                case INTEGER:
                case STRING:
                case BOOLEAN: {
                    Object value = fieldMap.get(se.getName()).getValue();
                    if (value instanceof Map.Entry) {
                        Map.Entry<Boolean, Integer> bi = (Map.Entry) value;
                        Boolean v = bi.getKey();
                        Integer m = bi.getValue();
                        jo.put("value", v);
                        jo.put("multi", m);
                        json.put(se.getName(), jo);
                    }
                    break;
                }
                default: {
                    Object value = fieldMap.get(se.getName()).getValue();
                    jo.put("value", value);
                    json.put(se.getName(), jo);
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
                            JSONObject jo = jso.getJSONObject(se.getName());
                            JSONArray ja = jo.getJSONArray("value");

                            LinkedList<String> items = new LinkedList<>();
                            for (int i = 0; i < ja.size(); i++) {
                                String v = ja.getString(i);
                                items.add(v);
                            }
                            result.put(se.getName(), items);
                            break;
                        }
                        case MULTI_STRING: {
                            JSONObject jo = jso.getJSONObject(se.getName());
                            JSONArray ja = jo.getJSONArray("value");

                            LinkedList<String> items = new LinkedList<>();
                            for (int i = 0; i < ja.size(); i++) {
                                String v = ja.getString(i);
                                items.add(v);
                            }
                            result.put(se.getName(), items);
                            break;
                        }
                        case INTEGER: {
                            JSONObject jo = jso.getJSONObject(se.getName());
                            String value = jo.getString("value");
                            String o = value;
                            result.put(se.getName(), o);
                            break;
                        }
                        case STRING: {
                            JSONObject jo = jso.getJSONObject(se.getName());
                            String value = jo.getString("value");
                            String o = value;
                            result.put(se.getName(), o);
                            break;
                        }
                        case BOOLEAN: {
                            JSONObject jo = jso.getJSONObject(se.getName());
                            String value = jo.getString("value");
                            Boolean o = Boolean.valueOf(value);
                            Integer i = null;
                            Object m = jo.get("multi");
                            if (m != null) {
                                i = Integer.parseInt(m.toString());
                            }
                            result.put(se.getName(), new AbstractMap.SimpleEntry<Boolean, Integer>(o, i));
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
