package com.again.spring.web.repository;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String>, QuerydslPredicateExecutor<Room> {

    @Query
    List<Room> findByHouse(House house);

    @Query
    List<Room> findByHouseAndType(House house, RoomType type);

    @Query
    List<Room> findByHouseId(String id);

    @Query
    List<Room> findByHouseIdAndType(String id, RoomType type);

    @Query
    List<Room> findByHouseIdAndAssignedTenant(String id, User assignedTenant);

    @Query("{ 'isClean' : ?0 }")
    List<Room> findByHouseIdAndStatus(String id, Boolean isClean);
}
