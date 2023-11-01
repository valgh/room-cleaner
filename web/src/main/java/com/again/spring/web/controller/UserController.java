package com.again.spring.web.controller;

import com.again.spring.web.model.User;
import com.again.spring.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable(required = true) String id) {
        Optional<User> result = this.userRepository.findById(id);
        return ControllerUtility.buildResponse(result);
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return this.userRepository.insert(user);
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity getUsersByUserName(@PathVariable(required = true) String userName) {
        List<User> result = this.userRepository.findUsersByUserName(userName);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity getUsersByName(@PathVariable(required = true) String name) {
        List<User> result = this.userRepository.findUsersByName(name);
        return ControllerUtility.buildResponse(result);
    }

    @PutMapping("/assign/{id}")
    public User assignHouse(@PathVariable(required = true) String id, @RequestParam(required = true) String houseId) {
        return null;
    }
}
