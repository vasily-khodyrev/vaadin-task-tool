package com.alu.tat.component;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by
 * User: vkhodyre
 * Date: 6/20/2016
 */
public class MultiStringBean implements Serializable {
    LinkedHashMap<String, Integer> values;

    public MultiStringBean(LinkedHashMap<String, Integer> values) {
        this.values = values;
    }
    public LinkedHashMap<String, Integer> getValues() {
        return values;
    }

    public void setValues(LinkedHashMap<String, Integer> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiStringBean that = (MultiStringBean) o;

        return values != null ? values.equals(that.values) : that.values == null;

    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }
}
