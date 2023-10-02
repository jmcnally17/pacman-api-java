package pacmanapi.unit.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pacmanapi.controller.UsersController;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UsersControllerTest {
  private final UserRepository repository = mock(UserRepository.class);
  private final User user = mock(User.class);
  private final MockedStatic<BCrypt> bCrypt = mockStatic(BCrypt.class);

  @AfterEach
  public void afterEach() {
    reset(repository, user);
    bCrypt.close();
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
    bCrypt.when(BCrypt::gensalt).thenReturn("SaltySalt");
    bCrypt.when(() -> BCrypt.hashpw("NootNoot", "SaltySalt")).thenReturn("SecretNootNoot");
    UsersController usersController = new UsersController(repository);

    usersController.createUser("Pingu", "NootNoot", user);
    bCrypt.verify(BCrypt::gensalt);
    bCrypt.verify(() -> BCrypt.hashpw("NootNoot", "SaltySalt"));
    verify(user).setUsername("Pingu");
    verify(user).setPassword("SecretNootNoot");
    verify(repository).save(user);
  }
}
