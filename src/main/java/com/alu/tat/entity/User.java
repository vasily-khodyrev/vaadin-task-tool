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

    @Column(name = "login")
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
        this.login = login;
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
}
