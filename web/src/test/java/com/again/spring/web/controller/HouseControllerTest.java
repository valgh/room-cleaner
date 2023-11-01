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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HouseControllerTest {
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
    private Room defaultRoom = new Room();
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
    public void shouldNotCreateInvalidHouse() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<House> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldCreateHouse() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);

        HttpEntity<House> request = new HttpEntity<>(newHouse, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        House received = objectMapper.readValue(result.getBody(), House.class);
        assertThat(received.getAddress()).isEqualTo(newHouse.getAddress());
        assertThat(received.getTenants().get(0).getName()).isEqualTo(newHouse.getTenants().get(0).getName());
    }

    @Test
    public void shouldNotFindHouseById() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"fakeid", String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldFindHouseById() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House houseAdded = houseController.createHouse(newHouse);

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+houseAdded.getId(), String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        House queriedHouse = objectMapper.readValue(result.getBody(), House.class);
        assertThat(queriedHouse.getAddress()).isEqualTo(newHouse.getAddress());
        assertThat(queriedHouse.getTenants().get(0).getUserName()).isEqualTo(newHouse.getTenants().get(0).getUserName());
    }

    @Test
    public void shouldNotFindByTenantId() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"tenant/fakeid", String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldFindByTenantId() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House houseAdded = houseController.createHouse(newHouse);

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"tenant/"+u.getId(), String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<House> results = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, House.class));
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getAddress()).isEqualTo(newHouse.getAddress());
    }

    @Test
    public void shouldNotFindByAddress() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        Map<String, String> parameters = Collections.singletonMap("address", "fakeaddress");
        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"address?addr={address}", String.class, parameters);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldFindByAddress() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 62, London");
        House houseAdded = houseController.createHouse(newHouse);
        Map<String, String> parameters = Collections.singletonMap("address", "Grimmauld Place, 62, London");

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"address?addr={address}", String.class, parameters);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<House> results = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, House.class));
        assertThat(results.size()).isGreaterThan(0);
        assertThat(results.get(0).getAddress()).isEqualTo(newHouse.getAddress());
    }

    @Test
    public void shouldNotAddTenant() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<User> request = new HttpEntity<>(alice01, headers);

        ResponseEntity<String> result = restTemplate.exchange(baseUrl+"fakeid/add", HttpMethod.PUT, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotRemoveTenant() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        Map<String, String> params = Collections.singletonMap("userId", "fake");
        ResponseEntity<String> result = restTemplate.exchange(baseUrl+"fakeid/remove?userId={userId}", HttpMethod.PUT, null, String.class, params);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotAddRoom() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Room> request = new HttpEntity<>(defaultRoom, headers);

        ResponseEntity<String> result = restTemplate.exchange(baseUrl+"fakeid/room/add", HttpMethod.PUT, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotRemoveRoom() {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        Map<String, String> parameters = Collections.singletonMap("roomId", "fakefakefake");
        ResponseEntity<String> result = restTemplate.exchange(baseUrl+"fakeid/room/remove?roomId={roomId}", HttpMethod.PUT, null, String.class, parameters);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldAddTenant() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        User u = userController.createUser(alice01);
        newHouse.addTenant(u);
        House houseAdded = houseController.createHouse(newHouse);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<User> request = new HttpEntity<>(u, headers);

        ResponseEntity<String> result = restTemplate.exchange(baseUrl+houseAdded.getId()+"/add", HttpMethod.PUT, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        House patchedHouse = objectMapper.readValue(result.getBody(), House.class);
        assertThat(patchedHouse.getAddress()).isEqualTo(houseAdded.getAddress());
        assertThat(patchedHouse.getTenants().size()).isGreaterThan(0);
        assertThat(patchedHouse.getTenants().get(0).getName()).isEqualTo(u.getName());
    }

    @Test
    public void shouldAddRoom() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/houses/";
        final House newHouse = new House();
        newHouse.setAddress("Grimmauld Place, 58, London");
        House houseAdded = houseController.createHouse(newHouse);
        defaultRoom.setType(RoomType.BEDROOM);
        defaultRoom.setHouse(houseAdded);
        Room r = roomController.createRoom(defaultRoom);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Room> request = new HttpEntity<>(r, headers);

        ResponseEntity<String> result = restTemplate.exchange(baseUrl+houseAdded.getId()+"/room/add", HttpMethod.PUT, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        House patchedHouse = objectMapper.readValue(result.getBody(), House.class);
        assertThat(patchedHouse.getAddress()).isEqualTo(houseAdded.getAddress());
        assertThat(patchedHouse.getRooms().size()).isGreaterThan(0);
        assertThat(patchedHouse.getRooms().get(0).getType()).isEqualTo(RoomType.BEDROOM);
        assertThat(patchedHouse.getRooms().get(0).getHouse().getId()).isEqualTo(houseAdded.getId());
    }
}
