package com.again.spring.web.controller;

import com.again.spring.web.model.User;
import com.again.spring.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/id/{id}")
    public User getUserById(@PathVariable String id) throws RuntimeException{
        Optional<User> result = this.userRepository.findById(id);
        if (result.isEmpty()) {
            throw new RuntimeException();
        }
        return result.get();
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return this.userRepository.insert(user);
    }

    @GetMapping("/username/{userName}")
    public List<User> getUsersByUserName(@PathVariable String userName) {
        return this.userRepository.findUsersByUserName(userName);
    }

    @GetMapping("/name/{name}")
    public List<User> getUsersByName(@PathVariable String name) {
        return this.userRepository.findUsersByName(name);
    }

}
