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

    @PostMapping("/delete/{id}")
    public void deleteRoom(@PathVariable String id) { this.roomRepository.deleteById(id) ;}

    @GetMapping("/house/{houseId}")
    public List<Room> getRoomsInHouse(@PathVariable String houseId) {
        return this.roomRepository.findByHouseId(houseId);
    }

    @GetMapping("/house/{houseId}/type/{type}")
    public List<Room> getRoomsInHouseByType(@PathVariable String houseId, @PathVariable RoomType type) {
        return this.roomRepository.findByHouseIdAndType(houseId, type);
    }

    @GetMapping("/house/{houseId}/status")
    public List<Room> getRoomsInHouseByStatus(@PathVariable String houseId, @RequestParam Boolean status) {
        return this.roomRepository.findByHouseIdAndStatus(houseId, status);
    }

    @GetMapping("/house/{houseId}/tenant")
    public List<Room> getRoomsInHouseByAssignedTenant(@PathVariable String houseId, @RequestParam User tenant) {
        return this.roomRepository.findByHouseIdAndAssignedTenant(houseId, tenant);
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
}
