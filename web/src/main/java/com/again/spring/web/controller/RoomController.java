package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.model.User;
import com.again.spring.web.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
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
    public ResponseEntity getRoomsInHouse(@PathVariable String houseId) {
        List<Room> result = this.roomRepository.findByHouseId(houseId);
        return buildResponse(result);
    }

    @GetMapping("/house/{houseId}/type/{type}")
    public ResponseEntity getRoomsInHouseByType(@PathVariable String houseId, @PathVariable RoomType type) {
        List<Room> result = this.roomRepository.findByHouseIdAndType(houseId, type);
        return buildResponse(result);
    }

    @GetMapping("/house/{houseId}/status")
    public ResponseEntity getRoomsInHouseByStatus(@PathVariable String houseId, @RequestParam Boolean status) {
        List<Room> result = this.roomRepository.findByHouseIdAndIsClean(houseId, status);
        return buildResponse(result);
    }

    @GetMapping("/house/{houseId}/tenant")
    public ResponseEntity getRoomsInHouseByAssignedTenantId(@PathVariable String houseId, @RequestParam String id) {
        List<Room> result = this.roomRepository.findByHouseIdAndAssignedTenantId(houseId, id);
        return buildResponse(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity setRoomStatus(@PathVariable String id, @RequestParam Boolean status) {
        Optional<Room> optionalRoom = this.roomRepository.findById(id);
        if (optionalRoom.isPresent()) {
            Room r = optionalRoom.get();
            r.setClean(status);
            return ResponseEntity.ok(this.roomRepository.save(r));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/assign/")
    public ResponseEntity assignTenant(@PathVariable String id, @RequestParam String userId) {
        return null;
    }

    @PutMapping("/{id}/remove")
    public ResponseEntity removeTenant(@PathVariable String id) {
        return null;
    }

    private ResponseEntity buildResponse(List result) {
        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
}
