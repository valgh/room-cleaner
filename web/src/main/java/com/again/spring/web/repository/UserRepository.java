package com.again.spring.web.repository;

import com.again.spring.web.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


import java.util.List;

public interface UserRepository extends MongoRepository<User, String>, QuerydslPredicateExecutor<User> {

    @Query("{ 'name' : ?0 }")
    List<User> findUsersByName(String name);

    @Query("{ 'username' : ?0 }")
    List<User> findUsersByUserName(String userName);
}
