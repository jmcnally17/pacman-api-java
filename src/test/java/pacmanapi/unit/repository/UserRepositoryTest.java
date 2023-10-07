package pacmanapi.unit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest(properties = {"spring.data.mongodb.database=pacman-test"})
public class UserRepositoryTest {
  @Autowired
  private UserRepository repository;

  @AfterEach
  void afterEach() {
    repository.deleteAll();
  }

  @Test
  public void canSaveAndFindAUser() {
    User user = new User("Bob123", "password123");
    assertEquals(user, repository.save(user));

    User retrievedUser = repository.findByUsername("Bob123");
    assertEquals("Bob123", retrievedUser.getUsername());
    assertEquals("password123", retrievedUser.getPassword());
  }
}
