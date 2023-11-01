package com.again.spring.web.controller;

import com.again.spring.web.builders.HouseBuilder;
import com.again.spring.web.builders.RoomBuilder;
import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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
    private House defaultHouse = new HouseBuilder().setAddress("Grimmauld Place, 58, London").build();
    private Room defaultRoom = new RoomBuilder().setType(RoomType.BEDROOM).setClean(false).build();
    private Room r1 = new RoomBuilder().setType(RoomType.KITCHEN).setClean(false).build();
    private Room r2 = new RoomBuilder().setType(RoomType.LIVING_ROOM).setClean(true).build();
    @LocalServerPort
    int serverPort;

    @Test
    public void contextLoads() throws Exception {
        assertThat(houseController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(roomController).isNotNull();
        assertThat(restTemplate).isNotNull();
    }

    @Before
    public void init() {
        alice01 = userController.createUser(alice01);
        defaultHouse.addTenant(alice01);
        defaultHouse = houseController.createHouse(defaultHouse);
        defaultRoom.setHouse(defaultHouse);
        r1.setHouse(defaultHouse);
        r2.setHouse(defaultHouse);
        defaultRoom.setAssignedTenant(alice01);
        defaultRoom = roomController.createRoom(defaultRoom);
        r1 = roomController.createRoom(r1);
        r2 = roomController.createRoom(r2);
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
        Room newRoom = new RoomBuilder().setHouse(defaultHouse).setType(RoomType.KITCHEN).setTenant(alice01).build();

        HttpEntity<Room> request = new HttpEntity<>(newRoom, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Room received = objectMapper.readValue(result.getBody(), Room.class);
        assertThat(received.getType()).isEqualTo(RoomType.KITCHEN);
        assertThat(received.getHouse().getAddress()).isEqualTo(defaultHouse.getAddress());
        assertThat(received.getAssignedTenant().getId()).isEqualTo(alice01.getId());
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

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+defaultHouse.getId(), String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<Room> queriedRooms = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
        assertThat(queriedRooms.size()).isEqualTo(3);
    }

    @Test
    public void shouldFindRoomsInHouseByType() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+defaultHouse.getId()+"/type/"+RoomType.LIVING_ROOM, String.class);
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

        Map<String, Boolean> params = Collections.singletonMap("status", true);

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+defaultHouse.getId()+"/status?status={status}", String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<Room> queriedRooms = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
        assertThat(queriedRooms.size()).isEqualTo(1);
        assertThat(queriedRooms.get(0).getClean()).isEqualTo(Boolean.TRUE);
        assertThat(queriedRooms.get(0).getType()).isEqualTo(RoomType.LIVING_ROOM);
    }

    @Test
    public void shouldFindRoomInHouseByAssignedTenant() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, String> params = Collections.singletonMap("id", alice01.getId());

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/house/"+defaultHouse.getId()+"/tenant?id={id}", String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<Room> queriedRooms = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
        assertThat(queriedRooms.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void shouldSetStatus() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        assertThat(defaultRoom.getClean()).isEqualTo(Boolean.FALSE);

        Map<String, Boolean> params = Collections.singletonMap("status", true);

        ResponseEntity<String> result = restTemplate.exchange(baseUrl+"/"+defaultRoom.getId()+"/status?status={status}", HttpMethod.PUT, null, String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        Room patchedRoom = objectMapper.readValue(result.getBody(), Room.class);
        assertThat(patchedRoom.getClean()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void shouldNotAssignTenantWrongUserId() {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        Map<String, String> params = Collections.singletonMap("userId", "fake");
        ResponseEntity<String> result = restTemplate.exchange(baseUrl+defaultRoom.getId()+"/assign?userId={userId}", HttpMethod.PUT, null, String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotRemoveTenant() {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        ResponseEntity<String> result = restTemplate.exchange(baseUrl+"fakeid/remove", HttpMethod.PUT, null, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldAssignTenant() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        Map<String, String> params = Collections.singletonMap("userId", alice01.getId());
        ResponseEntity<String> result = restTemplate.exchange(baseUrl+defaultRoom.getId()+"/assign?userId={userId}", HttpMethod.PUT, null, String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Room received = objectMapper.readValue(result.getBody(), Room.class);
        assertThat(received.getAssignedTenant().getId()).isEqualTo(alice01.getId());
    }

    @Test
    public void shouldRemoveTenant() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/rooms/";
        ResponseEntity<String> result = restTemplate.exchange(baseUrl+defaultRoom.getId()+"/remove", HttpMethod.PUT, null, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Room received = objectMapper.readValue(result.getBody(), Room.class);
        assertThat(received.getAssignedTenant()).isNull();
    }

}
