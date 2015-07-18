package com.alu.tat.entity;

import com.alu.tat.entity.schema.Schema;

import javax.persistence.*;

/**
 * Created by imalolet on 6/10/2015.
 */
@NamedQueries({
        @NamedQuery(
                name = "findTaskByRelease",
                query = "from Task t where t.release = :release"
        ),
        @NamedQuery(
                name = "findTaskBySchema",
                query = "from Task t where t.schema = :schema"
        )
})
@Entity
@Table(name = "task")
public class Task extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schema_id")
    private Schema schema;

    @Column(name = "data")
    private String data;

    @Enumerated(EnumType.STRING)
    private Release release;

    public enum Release {
        OT11("11"), OT10("10");

        private String version;

        Release(String version) {
            this.version = version;
        }

        public String getVersion() {
            return version;
        }
    }

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return name;
    }
}
