package com.again.spring.web.repository;

import com.again.spring.web.model.House;
import com.again.spring.web.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends MongoRepository<House, String>, QuerydslPredicateExecutor<House> {
    @Query("{ 'address' : ?0 }")
    Optional<List<House>> findByAddress(String address);
    @Query
    Optional<List<House>> findByTenantsId(String id);
}
