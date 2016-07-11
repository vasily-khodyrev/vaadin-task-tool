package com.alu.tat.component;

import java.io.Serializable;

/**
 * Created by
 * User: vkhodyre
 * Date: 6/21/2016
 */
public class BooleanItemBean implements Serializable {
    Boolean value;
    Integer multi;
    String comments;

    public BooleanItemBean(Boolean value, Integer multi, String comments) {
        this.value = value;
        this.multi = multi;
        this.comments = comments;
    }

    public Boolean getValue() {
        return value;
    }

    public Integer getMulti() {
        return multi;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanItemBean that = (BooleanItemBean) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (multi != null ? !multi.equals(that.multi) : that.multi != null) return false;
        return !(comments != null ? !comments.equals(that.comments) : that.comments != null);

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (multi != null ? multi.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }
}
