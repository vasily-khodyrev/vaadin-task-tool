package com.alu.tat.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by imalolet on 6/10/2015.
 */
@MappedSuperclass
public class BaseEntity implements Serializable, Comparable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Id
    private Long id;

    @Column(name = "create_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Date createTime;

    @Version
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "is_system")
    private Boolean isSystem = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    @PrePersist
    void onCreate() {
        this.setUpdateTime(new Date());
    }

    @PreRemove
    void onRemove() {
        if (this.getIsSystem() != null && this.getIsSystem()) {
            throw new PersistenceException("It's prohibited to remove system objects!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((BaseEntity)o).id);
    }
}
