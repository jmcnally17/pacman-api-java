package pacmanapi.unit.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
  private UsersController usersController;

  @BeforeEach
  public void beforeEach() {
    usersController = new UsersController(repository);
  }

  @AfterEach
  public void afterEach() {
    reset(repository, user);
  }

  @Test
  public void getUserFindsAUserFromTheRepository() {
    String username = "Pingu";
    HashMap<String, String> userMap = new HashMap<>();
    userMap.put("username", username);
    HashMap<String, HashMap<String, String>> userData = new HashMap<>();
    userData.put("user", userMap);

    when(user.getUsername()).thenReturn(username);
    when(repository.findByUsername(username)).thenReturn(user);

    assertEquals(userData, usersController.getUser(username));
    verify(repository).findByUsername(username);
    verify(user).getUsername();
  }

  @Test
  public void createUserSavesAUser() {
    MockedStatic<BCrypt> bCryptMockedStatic = mockStatic(BCrypt.class);
    String salt = "SaltyMcSaltFace";
    String username = "Pingu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);
    String encryptedPassword = "SecretNootNoot";

    bCryptMockedStatic.when(BCrypt::gensalt).thenReturn(salt);
    bCryptMockedStatic.when(() -> BCrypt.hashpw(password, salt)).thenReturn(encryptedPassword);

    usersController.createUser(requestBody, user);
    bCryptMockedStatic.verify(BCrypt::gensalt);
    bCryptMockedStatic.verify(() -> BCrypt.hashpw(password, salt));
    verify(user).setUsername(username);
    verify(user).setPassword(encryptedPassword);
    verify(repository).save(user);

    bCryptMockedStatic.close();
  }
}
