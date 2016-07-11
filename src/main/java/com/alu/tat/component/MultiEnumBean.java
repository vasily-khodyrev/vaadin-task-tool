package com.alu.tat.component;

import java.io.Serializable;
import java.util.List;

/**
 * Created by
 * User: Vasily Khodyrev
 * Date: 23.06.2016
 */
public class MultiEnumBean implements Serializable {
    List<String> value;

    public MultiEnumBean(List<String> value) {
        this.value = value;
    }

    public List<String> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiEnumBean that = (MultiEnumBean) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
