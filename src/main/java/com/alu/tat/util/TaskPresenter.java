package com.alu.tat.util;

import com.alu.tat.component.BooleanItemBean;
import com.alu.tat.component.MultiStringBean;
import com.alu.tat.entity.Task;
import com.alu.tat.entity.schema.Schema;
import com.alu.tat.entity.schema.SchemaElement;
import com.alu.tat.service.SchemaService;
import com.vaadin.data.Property;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;

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
                        BooleanItemBean bi = (BooleanItemBean) value;
                        if (!bi.getValue()) {
                            continue;
                        }
                        Integer multi = bi.getMulti();
                        if (multi == null) {
                            multi = se.getMultiplier();
                        }
                        String comment = bi.getComments();
                        estim += multi;
                        result.append("<b>" + se.getName() + ":</b> " + "yes");
                        if (se.getMultiplier() > 0) {
                            result.append(" - " + getDaysPrint(multi));
                        }
                        if (!StringUtils.isBlank(comment)) {
                            result.append("<br/> Comment: " + putString(comment));
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
                    case MULTI_TEXT:
                    case MULTI_STRING: {
                        MultiStringBean bean = (MultiStringBean) value;
                        LinkedHashMap<String, Integer> items = bean.getValues();
                        Integer multi = bean.getMulti();
                        Integer itemMulti = 0;
                        for (Map.Entry<String, Integer> item : items.entrySet()) {
                            itemMulti += item.getValue();
                        }
                        result.append("<b>" + se.getName() + ":</b> Total " + getDaysPrint(itemMulti) + "<br>");
                        int i = 1;
                        for (Map.Entry<String, Integer> item : items.entrySet()) {
                            result.append("<b> Option " + i + ":</b> " + getDaysPrint(item.getValue()) + "<br>");
                            result.append(putString(item.getKey()) + "<br><br>");
                            i++;
                        }
                        estim += itemMulti;
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
                case MULTI_TEXT:
                case MULTI_STRING: {
                    MultiStringBean bean = (MultiStringBean) fieldMap.get(se.getName()).getValue();
                    LinkedHashMap<String, Integer> items = bean.getValues();
                    Integer multi = bean.getMulti();
                    if (items != null) {
                        JSONArray ja = new JSONArray();
                        for (Map.Entry<String, Integer> item : items.entrySet()) {
                            JSONObject itemJo = new JSONObject();
                            itemJo.put("item", item.getKey());
                            itemJo.put("multi", item.getValue());
                            ja.add(itemJo);
                        }
                        jo.put("value", ja);
                        jo.put("multi", multi);
                        json.put(se.getName(), jo);
                    }
                    break;
                }
                case INTEGER:
                case STRING:
                case BOOLEAN: {
                    Object value = fieldMap.get(se.getName()).getValue();
                    if (value instanceof BooleanItemBean) {
                        BooleanItemBean bi = (BooleanItemBean) value;
                        Boolean v = bi.getValue();
                        Integer m = bi.getMulti();
                        String c = bi.getComments();
                        jo.put("value", v);
                        jo.put("multi", m);
                        jo.put("comment", c);
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
                        case MULTI_TEXT:
                        case MULTI_STRING: {
                            JSONObject jo = jso.getJSONObject(se.getName());
                            JSONArray ja = jo.getJSONArray("value");

                            LinkedHashMap<String, Integer> items = new LinkedHashMap<>();
                            for (int i = 0; i < ja.size(); i++) {
                                JSONObject v = ja.getJSONObject(i);
                                String item = v.getString("item");
                                Integer multi = v.getInt("multi");
                                items.put(item, multi);
                            }

                            Integer i = null;
                            Object m = jo.get("multi");
                            if (m != null) {
                                i = Integer.parseInt(m.toString());
                            }
                            result.put(se.getName(), new MultiStringBean(i, items));
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
                            String c = null;
                            Object co = jo.get("comment");
                            if (co != null) {
                                c = co.toString();
                            }
                            result.put(se.getName(), new BooleanItemBean(o, i, c));
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
