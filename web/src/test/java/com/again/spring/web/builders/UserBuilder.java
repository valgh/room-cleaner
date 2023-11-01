package com.again.spring.web.builders;

import com.again.spring.web.model.User;

public class UserBuilder {

    private String username;
    private String name;

    public UserBuilder(){}

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public User build() {
        return new User(this.username, this.name);
    }
}
