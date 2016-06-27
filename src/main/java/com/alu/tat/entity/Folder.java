package com.alu.tat.entity;

import javax.persistence.*;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/23/2016
 */
@Entity
@Table(name = "folder")
public class Folder  extends BaseEntity  {
    @Column(name = "name",unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "folder_id")
    private Folder root;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getRoot() {
        return root;
    }

    public void setRoot(Folder root) {
        this.root = root;
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

        Folder folder = (Folder) o;

        return name.equals(folder.name);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((Folder)o).name);
    }
}
