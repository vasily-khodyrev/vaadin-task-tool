package com.alu.tat.entity;

import javax.persistence.*;

/**
 * Created by imalolet on 6/10/2015.
 */
@NamedQueries({
        @NamedQuery(
                name = "findUserByLogin",
                query = "from User u where u.login = :login"
        )
})
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "pwd_hash")
    private String passwordHash;

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login.toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return login.equals(user.login);

    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        return result;
    }

    @PreRemove
    void onRemove() {
        if (this.getIsSystem() != null && this.getIsSystem()) {
            throw new PersistenceException("It's prohibited to remove system objects!");
        }
    }
}
