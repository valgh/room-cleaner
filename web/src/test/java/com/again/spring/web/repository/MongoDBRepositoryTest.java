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
public class MongoDBRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private RoomRepository roomRepository;

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

    /*
    ============ USERS TESTS
     */

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

    /*
    ============ HOUSE TESTS
     */


    @Test
    public void testSaveHouse() {
        final User user = new User();
        user.setUserName("tenant");
        user.setName("superTenant");
        userRepository.save(user);

        final User tenant = mongoTemplate.findOne(Query.query(Criteria.where("userName").is("tenant")), User.class);

        final House house = new House();
        house.setAddress("pppx, 71");
        house.setClean(Boolean.TRUE);
        house.addTenant(tenant);

        houseRepository.save(house);
        assertThat(
                mongoTemplate.findOne(Query.query(Criteria.where("address").is("pppx, 71")), House.class)
                        .getAddress()
        ).isEqualTo("pppx, 71");
    }

    @Test
    public void testFindHouseByAddress() {
        final House house = new House();
        house.setAddress("address, 71");

        houseRepository.save(house);
        assertThat(
                houseRepository.findByAddress("address, 71").get(0).getAddress()
        ).isEqualTo("address, 71");
    }

    @Test
    public void testFindCleanHouse() {
        final House house = new House();
        house.setClean(Boolean.TRUE);
        houseRepository.save(house);

        assertThat(
                houseRepository.findCleanHouse().get(0).getClean()
        ).isEqualTo(Boolean.TRUE);
    }

    /*
    ============ ROOM TESTS
     */


    @Test
    public void testSaveRoom() {
        final User user = new User();
        user.setUserName("vgxz");
        user.setName("vvbc");
        userRepository.save(user);

        final User tenant = mongoTemplate.findOne(Query.query(Criteria.where("userName").is("vgxz")), User.class);

        final House house = new House();
        house.setAddress("lptc, 51, rm");
        house.addTenant(tenant);
        houseRepository.save(house);

        final House persistedHouse = mongoTemplate.findOne(Query.query(Criteria.where("address").is("lptc, 51, rm")), House.class);
        assert tenant != null;
        tenant.setHouse(persistedHouse);
        userRepository.save(tenant); // update

        final Room room = new Room();
        room.setHouse(persistedHouse);
        room.setAssignedTenant(tenant);
        room.setType(RoomType.BEDROOM);
        roomRepository.save(room);

        assertThat(
                mongoTemplate.findOne(Query.query(Criteria.where("type").is("BEDROOM")), Room.class)
                        .getHouse().getAddress()
        ).isEqualTo("lptc, 51, rm");
    }

    @Test
    public void findByHouseAndRoomType() {
        final House house = new House();
        house.setAddress("lptc, 81, mi");
        houseRepository.save(house);

        final House persistedHouse = mongoTemplate.findOne(Query.query(Criteria.where("address").is("lptc, 81, mi")), House.class);

        final Room room = new Room();
        room.setHouse(persistedHouse);
        room.setType(RoomType.KITCHEN);
        roomRepository.save(room);

        assertThat(
                roomRepository.findByHouseAndType(persistedHouse, RoomType.KITCHEN).get(0).getType()
        ).isEqualTo(RoomType.KITCHEN);
    }
}
