package repository;

import com.again.spring.web.config.MongoConfig;
import com.again.spring.web.model.User;
import com.again.spring.web.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MongoConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Before
    public void setup() {
        if (!mongoOperations.collectionExists(User.class)) {
            mongoOperations.createCollection(User.class);
        }
    }

    @After
    public void clean() {
        mongoOperations.dropCollection(User.class);
    }

    @Test
    public void testSaveUser() {
        final User user = new User();
        user.setUserName("username01");

        userRepository.save(user);
        assertThat(
                mongoOperations.findOne(Query.query(Criteria.where("userName").is("username01")), User.class).getUserName()
        ).isEqualTo("username01");
    }
}
