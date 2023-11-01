package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.User;
import com.again.spring.web.repository.HouseRepository;
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
@RequestMapping("/houses")
@Tag(name = "House", description = "House APIs")
public class HouseController {

    @Autowired
    private final HouseRepository houseRepository;

    public HouseController(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    @PostMapping("/create")
    @Operation(summary = "Create new house.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "House created.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid body",
                    content = @Content)})
    public House createHouse(@RequestBody(required = true) House house) {
        return this.houseRepository.insert(house);
    }
    @PutMapping("/{id}/room/add")
    @Operation(summary = "Add a room to the house.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter or body.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
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
    @Operation(summary = "Remove a room from the house.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room removed.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter or body",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
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
    @Operation(summary = "Get house by address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "House retrieved.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
    public ResponseEntity getByAddress(@RequestParam(required = true, name = "addr") String address) {
        Optional<List<House>> result = this.houseRepository.findByAddress(address);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get house by its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "House retrieved.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
    public ResponseEntity getById(@PathVariable(required = true) String id) {
        Optional<House> result = this.houseRepository.findById(id);
        return ControllerUtility.buildResponse(result);
    }

    @GetMapping("/tenant/{id}")
    @Operation(summary = "Get house by its tenant id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "House retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
    public ResponseEntity getByTenantId(@PathVariable(required = true) String id) {
        Optional<List<House>> result = this.houseRepository.findByTenantsId(id);
        return ControllerUtility.buildResponse(result);
    }

    @PutMapping("/{id}/add")
    @Operation(summary = "Add a new tenant for the house.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant added.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter or body.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House or tenant not found.",
                    content = @Content) })
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
    @Operation(summary = "Remove a tenant from the house.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant removed.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = House.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "House not found.",
                    content = @Content) })
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
