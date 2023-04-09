package com.again.spring.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public class Room {

    @Id
    private String id;
    @DocumentReference
    private House house;
    @DocumentReference
    private User assignedTenant;
    private RoomType type;
    private Boolean isClean;

    public Room(House house, RoomType type) {
        this.house = house;
        this.type = type;
    }

    public Room() {

    }

    public String getId() {
        return id;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Boolean getClean() {
        return isClean;
    }

    public void setClean(Boolean clean) {
        isClean = clean;
    }

    public User getAssignedTenant() {
        return assignedTenant;
    }

    public void setAssignedTenant(User assignedTenant) {
        this.assignedTenant = assignedTenant;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", house=" + house +
                ", assignedTenant=" + assignedTenant +
                ", type=" + type +
                ", isClean=" + isClean +
                '}';
    }
}
