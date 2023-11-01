package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.User;
import com.again.spring.web.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/houses")
public class HouseController {

    @Autowired
    private final HouseRepository houseRepository;

    public HouseController(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    @PostMapping("/create")
    public House createHouse(@RequestBody(required = true) House house) {
        return this.houseRepository.insert(house);
    }
    @PutMapping("/{id}/room/add")
    public ResponseEntity addRoom(@PathVariable(required = true) String id, @RequestBody(required = true) Room room) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.addRoom(room);
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/room/remove")
    public ResponseEntity removeRoom(@PathVariable(required = true) String id, @RequestParam(required = true) String roomId) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.getRooms().removeIf(room -> roomId.equals(room.getId()));
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/address")
    public ResponseEntity getByAddress(@RequestParam(required = true, name = "addr") String address) {
        Optional<List<House>> result = this.houseRepository.findByAddress(address);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable(required = true) String id) {
        Optional<House> result = this.houseRepository.findById(id);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/tenant/{id}")
    public ResponseEntity getByTenantId(@PathVariable(required = true) String id) {
        Optional<List<House>> result = this.houseRepository.findByTenantsId(id);
        return ControllerUtility.buildResponse(result);
    }

    @PutMapping("/{id}/add")
    public ResponseEntity addTenant(@PathVariable(required = true) String id, @RequestBody(required = true) User user) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.addTenant(user);
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/remove")
    public ResponseEntity removeTenant(@PathVariable(required = true) String id, @RequestParam(required = true) String userId) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.getTenants().removeIf(user -> userId.equals(user.getId()));
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }
}
