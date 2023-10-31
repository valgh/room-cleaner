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
    public ResponseEntity getUserById(@PathVariable String id) {
        Optional<User> result = this.userRepository.findById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return this.userRepository.insert(user);
    }

    @GetMapping("/{userName}")
    public List<User> getUsersByUserName(@PathVariable String userName) {
        return this.userRepository.findUsersByUserName(userName);
    }

    @GetMapping("/{name}")
    public List<User> getUsersByName(@PathVariable String name) {
        return this.userRepository.findUsersByName(name);
    }

    @PatchMapping("/{id}")
    public User assignHouse(@PathVariable String id, @RequestParam String houseId) {
        return null;
    }
}
