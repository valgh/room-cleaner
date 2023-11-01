package com.again.spring.web.builders;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.User;

import java.util.List;

public class HouseBuilder {

    private List<Room> rooms;
    private List<User> tenants;
    private String address;

    public HouseBuilder setRooms(List<Room> rooms) {
        this.rooms = rooms;
        return this;
    }

    public HouseBuilder setTenants(List<User> tenants) {
        this.tenants = tenants;
        return this;
    }

    public HouseBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public House build() {
        House h = new House();
        if (this.rooms != null) {
            this.rooms.forEach(h::addRoom);
        }
        if (this.tenants != null) {
            this.tenants.forEach(h::addTenant);
        }
        h.setAddress(this.address);
        return h;
    }
}
