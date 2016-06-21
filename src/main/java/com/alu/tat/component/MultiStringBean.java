package com.alu.tat.component;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by
 * User: vkhodyre
 * Date: 6/20/2016
 */
public class MultiStringBean {
    Integer multi;
    LinkedHashMap<String, Integer> values;

    public MultiStringBean(Integer multi, LinkedHashMap<String, Integer> values) {
        this.multi = multi;
        this.values = values;
    }

    public Integer getMulti() {
        return multi;
    }

    public void setMulti(Integer multi) {
        this.multi = multi;
    }

    public LinkedHashMap<String, Integer> getValues() {
        return values;
    }

    public void setValues(LinkedHashMap<String, Integer> values) {
        this.values = values;
    }
}
