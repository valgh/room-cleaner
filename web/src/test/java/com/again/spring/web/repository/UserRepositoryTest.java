package com.again.spring.web.repository;

import com.again.spring.web.config.MongoConfig;
import com.again.spring.web.model.House;
import com.again.spring.web.model.Room;
import com.again.spring.web.model.RoomType;
import com.again.spring.web.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MongoConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
    ============ TESTS
    In order to run these Unit tests, an instance of MongoDB is required,
    connected to a DB whose name is "test" (but this can be changed in the
    code).
    ============
     */


    @Before
    public void setup() {
        if (!mongoTemplate.collectionExists(User.class)) {
            mongoTemplate.createCollection(User.class);
        }
    }

    @After
    public void clean() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void testSaveUser() {
        final User user = new User();
        user.setUserName("username01");
        user.setName("name01");

        userRepository.save(user);
        assertThat(
                mongoTemplate.findOne(Query.query(Criteria.where("userName").is("username01")), User.class)
                        .getUserName()
        ).isEqualTo("username01");
        assertThat(
                mongoTemplate.findOne(Query.query(Criteria.where("userName").is("username01")), User.class)
                        .getName()
        ).isEqualTo("name01");
    }

    @Test
    public void testFindUser() {
        final User user = new User();
        user.setUserName("valgx");
        user.setName("vvv");

        userRepository.save(user);
        assertThat(userRepository.findUsersByUserName("valgx").get(0).getUserName()).isEqualTo("valgx");
        assertThat(userRepository.findUsersByName("vvv").get(0).getName()).isEqualTo("vvv");
    }
}
