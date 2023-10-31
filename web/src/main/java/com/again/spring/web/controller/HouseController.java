package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.User;
import com.again.spring.web.repository.HouseRepository;
import org.apache.coyote.Response;
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
    public House createHouse(@RequestBody House house) {
        return this.houseRepository.insert(house);
    }

    @GetMapping("/address")
    public ResponseEntity getByAddress(@RequestParam String address) {
        List<House> result = this.houseRepository.findByAddress(address);
        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id) {
        Optional<House> result = this.houseRepository.findById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tenant/{id}")
    public ResponseEntity findByTenantId(@PathVariable String id) {
        List<House> result = this.houseRepository.findByTenantsId(id);
        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/add")
    public ResponseEntity addTenant(@PathVariable String id, @RequestBody User user) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.addTenant(user);
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/remove")
    public ResponseEntity removeTenant(@PathVariable String id, @RequestBody User user) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.deleteTenant(user);
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }
}
