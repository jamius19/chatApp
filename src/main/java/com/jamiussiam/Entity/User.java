package com.jamiussiam.Entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    private int id;

    private String name;

    private String email;

    private String password;

    private String sqAnswer;

    @OneToMany(mappedBy = "user")
    private List<Password> prevPasswords = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ChatGroup> groups = new ArrayList<>();

    @OneToMany(mappedBy = "admin")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ChatGroup> adminGroups = new ArrayList<>();

    public List<ChatGroup> getAdminGroups() {
        return adminGroups;
    }

    public void setAdminGroups(List<ChatGroup> adminGroups) {
        this.adminGroups = adminGroups;
    }

    public List<ChatGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ChatGroup> groups) {
        this.groups = groups;
    }

    public String getSqAnswer() {
        return sqAnswer;
    }

    public void setSqAnswer(String sqAnswer) {
        this.sqAnswer = sqAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Password> getPrevPasswords() {
        return prevPasswords;
    }

    public void setPrevPasswords(List<Password> prevPasswords) {
        this.prevPasswords = prevPasswords;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", sqAnswer='" + sqAnswer + '\'' +
                ", prevPasswords=" + prevPasswords +
                '}';
    }
}
