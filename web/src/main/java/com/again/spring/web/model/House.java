package com.again.spring.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class House {

    @Id
    private String id;
    private String address;
    @DocumentReference(lazy = true)
    private List<Room> rooms;
    @DocumentReference
    private List<User> tenants;
    public House() {
        this.rooms = new ArrayList<>();
        this.tenants = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<User> getTenants() {
        return tenants;
    }

    public void addTenant(User tenant) {
        this.tenants.add(tenant);
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public void deleteRoom(Room room) { this.rooms.remove(room); }

    public void deleteTenant(User tenant) { this.tenants.remove(tenant); }

    public Double computeCleaningPercentage() {
        if (this.rooms != null && !this.rooms.isEmpty()) {
            int totalRooms = this.rooms.size();
            long cleanRooms = this.rooms.stream().map(room -> Boolean.TRUE.equals(room.getClean())).count();
            return (double) (cleanRooms/totalRooms);
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "House{" +
                "id='" + id + '\'' +
                ", address=" + address +
                ", rooms=" + rooms +
                ", tenants=" + tenants +
                ", cleaningPercentage=" + computeCleaningPercentage().toString() +
                '}';
    }
}
