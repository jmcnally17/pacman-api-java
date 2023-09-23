package pacmanapi.unit.model;

import org.junit.jupiter.api.Test;
import pacmanapi.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
  @Test
  public void hasAUsernameAndPassword() {
    User user = new User("Bob123", "password123");
    assertEquals("Bob123", user.getUsername());
    assertEquals("password123", user.getPassword());
  }

  @Test
  public void canSetItsUsername() {
    User user = new User();
    user.setUsername("Alan");
    assertEquals("Alan", user.getUsername());
  }

  @Test
  public void canSetItsPassword() {
    User user = new User();
    user.setPassword("anotherPassword");
    assertEquals("anotherPassword", user.getPassword());
  }
}
