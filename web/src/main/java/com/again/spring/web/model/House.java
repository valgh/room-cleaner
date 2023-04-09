package com.again.spring.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.repository.ExistsQuery;

import java.util.ArrayList;
import java.util.List;

@Document
public class House {

    @Id
    private String id;
    @DocumentReference(lazy = true)
    private List<Room> rooms;
    @DocumentReference(lazy = true)
    private List<User> tenants;
    private Boolean isClean;

    public House() {
        this.rooms = new ArrayList<>();
        this.tenants = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<User> getTenants() {
        return tenants;
    }

    public void setTenants(List<User> tenants) {
        this.tenants = tenants;
    }

    public Boolean getClean() {
        return isClean;
    }

    public void setClean(Boolean clean) {
        isClean = clean;
    }

    public Double computeCleaningPercentage() {
        if (!this.rooms.isEmpty()) {
            int totalRooms = this.rooms.size();
            long cleanRooms = this.rooms.stream().map(room -> room.getClean() == Boolean.TRUE).count();
            return (double) (cleanRooms/totalRooms);
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "House{" +
                "id='" + id + '\'' +
                ", rooms=" + rooms +
                ", tenants=" + tenants +
                ", isClean=" + isClean +
                '}';
    }
}
