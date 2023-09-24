package pacmanapi.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacmanapi.controller.UsersController;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UsersControllerTest {
  private final UserRepository repository = mock(UserRepository.class);
  private final User user = mock(User.class);

  @BeforeEach
  public void beforeEach() {
    reset(repository, user);
  }

  @Test
  public void getUserFindsAUserFromTheRepository() {
    when(user.getUsername()).thenReturn("UserMcUserface");
    UserRepository repository = mock(UserRepository.class);
    when(repository.findByUsername("UserMcUserface")).thenReturn(user);
    UsersController usersController = new UsersController(repository);

    HashMap<String, String> userMap = new HashMap<>();
    userMap.put("username", "UserMcUserface");
    HashMap<String, HashMap<String, String>> userData = new HashMap<>();
    userData.put("user", userMap);
    assertEquals(userData, usersController.getUser("UserMcUserface"));
    verify(repository).findByUsername("UserMcUserface");
    verify(user).getUsername();
  }

  @Test
  public void createUserSavesAUser() {
    UsersController usersController = new UsersController(repository);

    usersController.createUser("Pingu", "NootNoot", user);
    verify(user).setUsername("Pingu");
    verify(user).setPassword("NootNoot");
    verify(repository).save(user);
  }
}
