package com.again.spring.web.builders;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.model.User;

public class RoomBuilder {

    private House house;
    private RoomType type;
    private User assignedTenant;
    private Boolean isClean;

    public RoomBuilder setHouse(House house) {
        this.house = house;
        return this;
    }

    public RoomBuilder setType(RoomType type) {
        this.type = type;
        return this;
    }

    public RoomBuilder setTenant(User tenant) {
        this.assignedTenant = tenant;
        return this;
    }

    public RoomBuilder setClean(Boolean isClean) {
        this.isClean = isClean;
        return this;
    }

    public Room build() {
        Room r = new Room(this.house, this.type);
        r.setAssignedTenant(this.assignedTenant);
        r.setClean(this.isClean);
        return r;
    }

}
