package com.again.spring.web.repository;

import com.again.spring.web.model.House;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface HouseRepository extends MongoRepository<House, String>, QuerydslPredicateExecutor<House> {
}
