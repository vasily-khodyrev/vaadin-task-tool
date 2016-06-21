package com.alu.tat.component;

/**
 * Created by
 * User: vkhodyre
 * Date: 6/21/2016
 */
public class BooleanItemBean {
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
}
