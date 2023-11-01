package com.again.spring.web.controller;

import com.again.spring.web.model.User;
import com.again.spring.web.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "User APIs")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all the users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved all users.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "404", description = "No users found.",
                    content = @Content) })
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    public ResponseEntity getUserById(@PathVariable(required = true) String id) {
        Optional<User> result = this.userRepository.findById(id);
        return ControllerUtility.buildResponse(result);
    }

    @PostMapping("/create")
    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid body",
                    content = @Content)})
    public User createUser(@RequestBody User user) {
        return this.userRepository.insert(user);
    }

    @GetMapping("/username/{userName}")
    @Operation(summary = "Get user by its username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content) })
    public ResponseEntity getUsersByUserName(@PathVariable(required = true) String userName) {
        List<User> result = this.userRepository.findUsersByUserName(userName);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get user by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "user not found",
                    content = @Content) })
    public ResponseEntity getUsersByName(@PathVariable(required = true) String name) {
        List<User> result = this.userRepository.findUsersByName(name);
        return ControllerUtility.buildResponse(result);
    }
}
