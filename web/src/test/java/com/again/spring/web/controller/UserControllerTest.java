package com.again.spring.web.controller;

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
        final User newUser = new User();
        newUser.setUserName("username");
        newUser.setName("name");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<User> request = new HttpEntity<>(newUser, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(baseUrl+"create", request, String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.hasBody()).isEqualTo(Boolean.TRUE);
        User response = objectMapper.readValue(result.getBody(), User.class);
        assertThat(response.getName()).isEqualTo(newUser.getName());
        assertThat(response.getUserName()).isEqualTo(newUser.getUserName());
        assertThat(response.getHouse()).isNull();
    }
}
