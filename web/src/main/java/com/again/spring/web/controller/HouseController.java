package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.User;
import com.again.spring.web.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/address/{address}")
    public List<House> getByAddress(@PathVariable String address) {
        return this.houseRepository.findByAddress(address);
    }

    @GetMapping("/clean")
    public List<House> findAllClean() {
        return this.houseRepository.findCleanHouse();
    }

    @GetMapping("/tenants")
    public List<House> findByTenants(@RequestParam List<User> tenants) {
        return this.houseRepository.findByTenantsIn(tenants);
    }

    @GetMapping("/tenant/{id}")
    public List<House> findByTenantId(@PathVariable String id) {
        return this.houseRepository.findByTenantsId(id);
    }
}
