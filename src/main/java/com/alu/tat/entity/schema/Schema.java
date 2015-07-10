package com.alu.tat.entity.schema;

import com.alu.tat.entity.BaseEntity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/8/2015
 */
@Entity
@Table(name = "schema")
public class Schema extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "desc")
    private String description;


    //@CollectionTable(name = "SchemaElement", joinColumns = {@JoinColumn(name = "schema_name")})
    @ElementCollection (fetch = FetchType.EAGER)
    private List<SchemaElement> elementsList = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SchemaElement> getElementsList() {
        return elementsList;
    }

    public void setElementsList(List<SchemaElement> elementsList) {
        this.elementsList = elementsList;
    }

    @Override
    public String toString() {
        return name;
    }
}
