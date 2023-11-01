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
    public Room createRoom(@RequestBody(required = true) Room room) {
        return this.roomRepository.insert(room);
    }

    @GetMapping("/house/{houseId}")
    public ResponseEntity getRoomsInHouse(@PathVariable(required = true) String houseId) {
        List<Room> result = this.roomRepository.findByHouseId(houseId);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/house/{houseId}/type/{type}")
    public ResponseEntity getRoomsInHouseByType(@PathVariable(required = true) String houseId, @PathVariable(required = true) RoomType type) {
        List<Room> result = this.roomRepository.findByHouseIdAndType(houseId, type);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/house/{houseId}/status")
    public ResponseEntity getRoomsInHouseByStatus(@PathVariable(required = true) String houseId, @RequestParam(required = true) Boolean status) {
        List<Room> result = this.roomRepository.findByHouseIdAndIsClean(houseId, status);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/house/{houseId}/tenant")
    public ResponseEntity getRoomsInHouseByAssignedTenantId(@PathVariable(required = true) String houseId, @RequestParam(required = true) String id) {
        List<Room> result = this.roomRepository.findByHouseIdAndAssignedTenantId(houseId, id);
        return ControllerUtility.buildResponse(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity setRoomStatus(@PathVariable(required = true) String id, @RequestParam(required = true) Boolean status) {
        Optional<Room> optionalRoom = this.roomRepository.findById(id);
        if (optionalRoom.isPresent()) {
            Room r = optionalRoom.get();
            r.setClean(status);
            return ResponseEntity.ok(this.roomRepository.save(r));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/assign/")
    public ResponseEntity assignTenant(@PathVariable(required = true) String id, @RequestParam(required = true) String userId) {
        return null;
    }

    @PutMapping("/{id}/remove")
    public ResponseEntity removeTenant(@PathVariable(required = true) String id) {
        return null;
    }
}
