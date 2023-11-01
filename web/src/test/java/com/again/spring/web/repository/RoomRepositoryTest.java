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
public class RoomRepositoryTest {

    /*
    ============ TESTS
    In order to run these Unit tests, an instance of MongoDB is required,
    connected to a DB whose name is "test" (but this can be changed in the
    code).
    ============
     */

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Before
    public void setup() {
        if (!mongoTemplate.collectionExists(User.class)) {
            mongoTemplate.createCollection(User.class);
        }
        final User user = new User();
        user.setUserName("vgxz");
        user.setName("vvbc");
        userRepository.save(user);
        final House house = new House();
        house.setAddress("lptc, 51, rm");
        house.addTenant(user);
        houseRepository.save(house);
    }

    @After
    public void clean() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void testSaveRoom() {
        final User tenant = mongoTemplate.findOne(Query.query(Criteria.where("userName").is("vgxz")), User.class);
        final House persistedHouse = mongoTemplate.findOne(Query.query(Criteria.where("address").is("lptc, 51, rm")), House.class);

        assert tenant != null;
        tenant.addHouse(persistedHouse);
        userRepository.save(tenant);

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
        final House persistedHouse = mongoTemplate.findOne(Query.query(Criteria.where("address").is("lptc, 51, rm")), House.class);

        final Room room = new Room();
        room.setHouse(persistedHouse);
        room.setType(RoomType.KITCHEN);
        roomRepository.insert(room);
        persistedHouse.addRoom(room);
        houseRepository.save(persistedHouse);

        assertThat(
                roomRepository.findByHouseId(persistedHouse.getId()).get(0).getType()
        ).isEqualTo(RoomType.BEDROOM);

        assertThat(
                roomRepository.findByHouseIdAndType(persistedHouse.getId(), RoomType.KITCHEN).get(0).getType()
        ).isEqualTo(RoomType.KITCHEN);

        assertThat(
                roomRepository.findByHouseAndType(persistedHouse, RoomType.KITCHEN).get(0).getType()
        ).isEqualTo(RoomType.KITCHEN);
    }
}
