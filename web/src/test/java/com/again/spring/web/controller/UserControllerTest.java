package com.again.spring.web.controller;

import com.again.spring.web.builders.UserBuilder;
import com.again.spring.web.model.House;
import com.again.spring.web.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserController userController;
    private ObjectMapper objectMapper = new ObjectMapper();
    @LocalServerPort
    int serverPort;
    private final User u1 = new UserBuilder().setUsername("username").setName("name").build();
    private final User u2 = new UserBuilder().setUsername("username").setName("zzz").build();

    @Test
    public void contextLoads() throws Exception {
        assertThat(userController).isNotNull();
        assertThat(restTemplate).isNotNull();
    }

    @Test
    public void shouldNotSaveInvalidUser() {
        final String baseUrl = "http://localhost:"+serverPort+"/users/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<User> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldSaveNewUser() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/users/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<User> request = new HttpEntity<>(u1, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        User response = objectMapper.readValue(result.getBody(), User.class);
        assertThat(response.getName()).isEqualTo(u1.getName());
        assertThat(response.getUserName()).isEqualTo(u1.getUserName());
    }

    @Test
    public void shouldNotFindUserById() {
        final String baseUrl = "http://localhost:"+serverPort+"/users/";
        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/fakeid", String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldFindUserById() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/users/";
        User userAdded = userController.createUser(u1);

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/"+userAdded.getId(), String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        User queriedUser = objectMapper.readValue(result.getBody(), User.class);
        assertThat(queriedUser.getUserName()).isEqualTo(u1.getUserName());
        assertThat(queriedUser.getName()).isEqualTo(u1.getName());
    }

    @Test
    public void shouldNotFindUsersByName() {
        final String baseUrl = "http://localhost:"+serverPort+"/users/";
        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/name/xyxyxyv12", String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldFindUsersByName() throws JsonProcessingException {
        final String baseUrl = "http://localhost:"+serverPort+"/users/";
        userController.createUser(u2);
        userController.createUser(new UserBuilder().setUsername("username2").setName("zzz").build());

        ResponseEntity<String> result = restTemplate.getForEntity(baseUrl+"/name/zzz", String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        List<User> queriedUsers = objectMapper.readValue(result.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        assertThat(queriedUsers).isNotNull();
        assertThat(queriedUsers.size()).isGreaterThan(1);
        queriedUsers.forEach(user -> {
            assertThat(user.getName()).isEqualTo(u2.getName());
        });
    }
}
