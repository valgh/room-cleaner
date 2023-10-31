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
@RequestMapping("/house")
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
    public List<House> getByAddress(@RequestParam String address) {
        return this.houseRepository.findByAddress(address);
    }

    @GetMapping("/tenants")
    public List<House> findByTenants(@RequestBody List<User> tenants) {
        return this.houseRepository.findByTenantsIn(tenants);
    }

    @GetMapping("/tenant/{id}")
    public List<House> findByTenantId(@PathVariable String id) {
        return this.houseRepository.findByTenantsId(id);
    }

    @PatchMapping("/{id}/add/tenant")
    public ResponseEntity addTenant(@PathVariable String id, @RequestBody User user) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.addTenant(user);
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/delete/tenant")
    public ResponseEntity deleteTenant(@PathVariable String id, @RequestBody User user) {
        Optional<House> optionalHouse = this.houseRepository.findById(id);
        if (optionalHouse.isPresent()) {
            House h = optionalHouse.get();
            h.deleteTenant(user);
            return ResponseEntity.ok(this.houseRepository.save(h));
        }
        return ResponseEntity.notFound().build();
    }
}
