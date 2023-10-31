package com.again.spring.web.controller;

import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoomControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private HouseController houseController;
    @Autowired
    private RoomController roomController;
    @Autowired
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private User alice01 = new User("Alice", "alice01");
    @LocalServerPort
    int serverPort;

    @Test
    public void contextLoads() throws Exception {
        assertThat(houseController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(roomController).isNotNull();
        assertThat(restTemplate).isNotNull();
    }

    @Test
    public void shouldNotCreateInvalidRoom() {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Room> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldCreateRoom() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House h = houseController.createHouse(newHouse);

        Room r = roomBuilder(RoomType.KITCHEN, u, h, false);

        HttpEntity<Room> request = new HttpEntity<>(r, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Room received = objectMapper.readValue(result.getBody(), Room.class);
        assertThat(received.getType()).isEqualTo(RoomType.KITCHEN);
        assertThat(received.getHouse().getAddress()).isEqualTo(h.getAddress());
        assertThat(received.getAssignedTenant().getId()).isEqualTo(u.getId());
    }

    @Test
    public void shouldNotFindRoomsByHouseId() {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/fakeid", String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldFindRoomsByHouseId() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House h = houseController.createHouse(newHouse);

        Room r1 = roomBuilder(RoomType.KITCHEN, u, h, false);
        Room r2 = roomBuilder(RoomType.LIVING_ROOM, u, h, false);
        roomController.createRoom(r1);
        roomController.createRoom(r2);

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+h.getId(), String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<Room> queriedRooms = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
        assertThat(queriedRooms.size()).isEqualTo(2);
    }

    @Test
    public void shouldFindRoomsInHouseByType() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House h = houseController.createHouse(newHouse);

        Room r1 = roomBuilder(RoomType.KITCHEN, u, h, false);
        Room r2 = roomBuilder(RoomType.LIVING_ROOM, u, h, false);
        roomController.createRoom(r1);
        roomController.createRoom(r2);

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+h.getId()+"/type/"+RoomType.LIVING_ROOM, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<Room> queriedRooms = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
        assertThat(queriedRooms.size()).isEqualTo(1);
    }

    @Test
    public void shouldFindRoomInHouseByStatus() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House h = houseController.createHouse(newHouse);

        Room r1 = roomBuilder(RoomType.KITCHEN, u, h, true);
        Room r2 = roomBuilder(RoomType.LIVING_ROOM, u, h, false);
        roomController.createRoom(r1);
        roomController.createRoom(r2);

        Map<String, Boolean> params = Collections.singletonMap("status", true);

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+h.getId()+"/status?status={status}", String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<Room> queriedRooms = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
        assertThat(queriedRooms.size()).isEqualTo(1);
        assertThat(queriedRooms.get(0).getClean()).isEqualTo(Boolean.TRUE);
        assertThat(queriedRooms.get(0).getType()).isEqualTo(RoomType.KITCHEN);
    }

    @Test
    public void shouldFindRoomInHouseByAssignedTenant() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House h = houseController.createHouse(newHouse);

        Room r1 = roomBuilder(RoomType.KITCHEN, null, h, true);
        Room r2 = roomBuilder(RoomType.LIVING_ROOM, u, h, false);
        roomController.createRoom(r1);
        roomController.createRoom(r2);

        Map<String, String> params = Collections.singletonMap("id", u.getId());

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+h.getId()+"/tenant?id={id}", String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<Room> queriedRooms = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
        assertThat(queriedRooms.size()).isEqualTo(1);
        assertThat(queriedRooms.get(0).getType()).isEqualTo(RoomType.LIVING_ROOM);
    }

    @Test
    public void shouldSetStatus() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House h = houseController.createHouse(newHouse);

        Room r = roomBuilder(RoomType.LIVING_ROOM, u, h, false);
        roomController.createRoom(r);

        assertThat(r.getClean()).isEqualTo(Boolean.FALSE);

        Map<String, Boolean> params = Collections.singletonMap("status", true);

        ResponseEntity<String> result = restTemplate.exchange(baseUrl+"/"+r.getId()+"/status?status={status}", HttpMethod.PUT, null, String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        Room patchedRoom = objectMapper.readValue(result.getBody(), Room.class);
        assertThat(patchedRoom.getClean()).isEqualTo(Boolean.TRUE);
    }

    private Room roomBuilder(RoomType type, User tenant, House house, Boolean isClean) {
        Room r = new Room();
        r.setType(type);
        r.setAssignedTenant(tenant);
        r.setHouse(house);
        r.setClean(isClean);
        return r;
    }
}
