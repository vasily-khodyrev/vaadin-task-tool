package com.alu.tat.entity.schema;

import com.alu.tat.entity.BaseEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by
 * User: vkhodyre
 * Date: 7/8/2015
 */
@NamedQueries({
        @NamedQuery(
                name = "findNotDeprecatedSchemas",
                query = "from Schema s where s.deprecated = :deprecated"
        ),

        @NamedQuery(
                name = "getDefaultSchemas",
                query = "from Schema s where s.isdefault = :isdefault"
        )

})
@Entity
@Table(name = "schema")
public class Schema extends BaseEntity {
    @Column(name = "name", unique = true)
    private String name = "";

    @Column(name = "desc")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<SchemaElement> elementsList = new LinkedList<>();

    @Column(name = "isdefault")
    private Boolean isdefault = false;

    @Column(name = "deprecated")
    private Boolean deprecated = false;

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

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(Boolean isdefault) {
        this.isdefault = isdefault;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Schema schema = (Schema) o;

        if (!name.equals(schema.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((Schema) o).name);
    }

    public Schema copy(Schema s) {
        this.name = s.getName() + "(Copy)";
        this.description = s.getDescription();
        this.elementsList = new LinkedList(s.getElementsList());
        return this;
    }
}
