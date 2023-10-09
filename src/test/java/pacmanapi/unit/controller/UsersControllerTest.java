package pacmanapi.unit.controller;

import com.mongodb.DuplicateKeyException;
import com.mongodb.WriteConcernException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.server.ResponseStatusException;
import pacmanapi.controller.UsersController;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UsersControllerTest {
  private final UserRepository repository = mock(UserRepository.class);
  private final User user = mock(User.class);
  private final MockedStatic<BCrypt> bCryptMockedStatic = mockStatic(BCrypt.class);
  private UsersController usersController;

  @BeforeEach
  public void beforeEach() {
    usersController = new UsersController(repository);
  }

  @AfterEach
  public void afterEach() {
    reset(repository, user);
    bCryptMockedStatic.close();
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
  }

  @Test
  public void createUserRequiresUsernameWithNoWhitespace() {
    String username = "Pin gu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.createUser(requestBody, user));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Username cannot contain any spaces", exception.getReason());
  }

  @Test
  public void createUserRequiresUsernameLongerThanTwoCharacters() {
    String username = "Pi";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.createUser(requestBody, user));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Username must be 3-15 characters long", exception.getReason());
  }

  @Test
  public void createUserRequiresUsernameShorterThanSixteenCharacters() {
    String username = "Pinguuuuuuuuuuuu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.createUser(requestBody, user));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Username must be 3-15 characters long", exception.getReason());
  }

  @Test
  public void createUserRequiresPasswordLongerThanSevenCharacters() {
    String username = "Pingu";
    String password = "NootNoo";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.createUser(requestBody, user));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Password must be at least 8 characters long", exception.getReason());
  }

  @Test
  public void createUserThrowsErrorWhenTryingToSaveUsernameAlreadyInUse() {
    String salt = "SaltyMcSaltFace";
    String username = "Pingu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);
    String encryptedPassword = "SecretNootNoot";
    DuplicateKeyException mongoException = mock(DuplicateKeyException.class);

    bCryptMockedStatic.when(BCrypt::gensalt).thenReturn(salt);
    bCryptMockedStatic.when(() -> BCrypt.hashpw(password, salt)).thenReturn(encryptedPassword);
    when(repository.save(user)).thenThrow(mongoException);
    when(mongoException.getMessage()).thenReturn("code=11000");

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.createUser(requestBody, user));
    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("Username already taken", exception.getReason());
    verify(user).setUsername(username);
    verify(user).setPassword(encryptedPassword);
    verify(repository).save(user);
    verify(mongoException).getMessage();
  }

  @Test
  public void createUserThrowsServerErrorWhenSaveFails() {
    String salt = "SaltyMcSaltFace";
    String username = "Pingu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);
    String encryptedPassword = "SecretNootNoot";
    WriteConcernException mongoException = mock(WriteConcernException.class);

    bCryptMockedStatic.when(BCrypt::gensalt).thenReturn(salt);
    bCryptMockedStatic.when(() -> BCrypt.hashpw(password, salt)).thenReturn(encryptedPassword);
    when(repository.save(user)).thenThrow(mongoException);
    when(mongoException.getMessage()).thenReturn("Some generic MongoDB error message");

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.createUser(requestBody, user));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    assertEquals("Some generic MongoDB error message", exception.getReason());
    verify(user).setUsername(username);
    verify(user).setPassword(encryptedPassword);
    verify(repository).save(user);
    verify(mongoException).getMessage();
  }
}
