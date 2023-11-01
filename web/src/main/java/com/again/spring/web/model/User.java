package com.again.spring.web.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    private String id;
    private String userName;
    private String name;
    @DocumentReference(lazy = true)
    private List<House> houses;

    public User(String userName, String name) {
        this.userName = userName;
        this.name = name;
        this.houses = new ArrayList<House>();
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<House> getHouses() {
        return this.houses;
    }

    public void addHouse(House house) {
        this.houses.add(house);
    }

    public void setHouses(List<House> houses) {
        this.houses = houses;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", house=" + houses +
                '}';
    }
}
