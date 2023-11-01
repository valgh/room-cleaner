package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.model.User;
import com.again.spring.web.repository.RoomRepository;
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
@RequestMapping("/rooms")
@Tag(name = "Room", description = "Room APIs")
public class RoomController {

    @Autowired
    private final RoomRepository roomRepository;
    @Autowired
    private final UserRepository userRepository;

    public RoomController(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room created.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content)})
    public Room createRoom(@RequestBody(required = true) Room room) {
        return this.roomRepository.insert(room);
    }

    @GetMapping("/house/{houseId}")
    @Operation(summary = "Get all the rooms in a given house by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms retrieved.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
    public ResponseEntity getRoomsInHouse(@PathVariable(required = true) String houseId) {
        List<Room> result = this.roomRepository.findByHouseId(houseId);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/house/{houseId}/type/{type}")
    @Operation(summary = "Get all the rooms of a single Room Type in a given house by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms retrieved.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
    public ResponseEntity getRoomsInHouseByType(@PathVariable(required = true) String houseId, @PathVariable(required = true) RoomType type) {
        List<Room> result = this.roomRepository.findByHouseIdAndType(houseId, type);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/house/{houseId}/status")
    @Operation(summary = "Get all the rooms with a clean or not clean status in a given house by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms retrieved.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
    public ResponseEntity getRoomsInHouseByStatus(@PathVariable(required = true) String houseId, @RequestParam(required = true) Boolean status) {
        List<Room> result = this.roomRepository.findByHouseIdAndIsClean(houseId, status);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/house/{houseId}/tenant")
    @Operation(summary = "Get all the rooms with an assigned tenant id in a given house by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms retrieved.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
    public ResponseEntity getRoomsInHouseByAssignedTenantId(@PathVariable(required = true) String houseId, @RequestParam(required = true) String id) {
        List<Room> result = this.roomRepository.findByHouseIdAndAssignedTenantId(houseId, id);
        return ControllerUtility.buildResponse(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Set a room status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room status set.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found.",
                    content = @Content) })
    public ResponseEntity setRoomStatus(@PathVariable(required = true) String id, @RequestParam(required = true) Boolean status) {
        Optional<Room> optionalRoom = this.roomRepository.findById(id);
        if (optionalRoom.isPresent()) {
            Room r = optionalRoom.get();
            r.setClean(status);
            return ResponseEntity.ok(this.roomRepository.save(r));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign a tenant to a room by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant assigned.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room or tenant not found.",
                    content = @Content) })
    public ResponseEntity assignTenant(@PathVariable(required = true) String id, @RequestParam(required = true) String userId) {
        Optional<Room> optionalRoom = this.roomRepository.findById(id);
        Optional<User> optionalUser = this.userRepository.findById(userId);
        if (optionalRoom.isPresent() && optionalUser.isPresent()) {
            Room r = optionalRoom.get();
            r.setAssignedTenant(optionalUser.get());
            return ResponseEntity.ok(this.roomRepository.save(r));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/remove")
    @Operation(summary = "Remove the assigned tenant from the room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant removed.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Room.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found.",
                    content = @Content) })
    public ResponseEntity removeTenant(@PathVariable(required = true) String id) {
        Optional<Room> optionalRoom = this.roomRepository.findById(id);
        if (optionalRoom.isPresent()) {
            Room r = optionalRoom.get();
            r.setAssignedTenant(null);
            return ResponseEntity.ok(this.roomRepository.save(r));
        }
        return ResponseEntity.notFound().build();
    }
}
