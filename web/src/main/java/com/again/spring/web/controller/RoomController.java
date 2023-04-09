package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping("/create")
    public Room createRoom(@RequestBody Room room) {
        return this.roomRepository.insert(room);
    }

    @GetMapping("/house/{houseId}")
    public List<Room> getRoomsInHouse(@PathVariable String houseId) {
        return this.roomRepository.findByHouseId(houseId);
    }

    @GetMapping("/house/{houseId}/type/{type}")
    List<Room> getRoomsInHouseByType(@PathVariable String houseId, @PathVariable RoomType type) {
        return this.roomRepository.findByHouseIdAndType(houseId, type);
    }
}
