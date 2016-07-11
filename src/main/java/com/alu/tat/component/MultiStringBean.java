package com.alu.tat.component;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by
 * User: vkhodyre
 * Date: 6/20/2016
 */
public class MultiStringBean implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiStringBean that = (MultiStringBean) o;

        if (multi != null ? !multi.equals(that.multi) : that.multi != null) return false;
        return !(values != null ? !values.equals(that.values) : that.values != null);

    }

    @Override
    public int hashCode() {
        int result = multi != null ? multi.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
