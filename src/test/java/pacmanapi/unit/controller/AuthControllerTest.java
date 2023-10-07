package pacmanapi.unit.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pacmanapi.controller.AuthController;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;
import pacmanapi.utility.Authenticator;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
  private final UserRepository repository = mock(UserRepository.class);
  private final Authenticator authenticator = mock(Authenticator.class);
  private AuthController authController;

  @BeforeEach
  public void beforeEach() {
    authController = new AuthController(repository, authenticator);
  }

  @AfterEach
  public void afterEach() {
    reset(repository, authenticator);
  }

  @Test
  public void authenticateTokenUsesAuthenticatorToAuthenticateAJsonWebToken() {
    String token = "SuperSecureToken";
    HashMap<String, String> header = new HashMap<>();
    header.put("authorization", token);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", "Pingu");
    HashMap<String, HashMap<String, String>> responseData = new HashMap<>();
    responseData.put("user", userData);

    when(authenticator.authenticateToken(token)).thenReturn(userData);

    assertEquals(responseData, authController.authenticateToken(header));
    verify(authenticator).authenticateToken(token);
  }

  @Test
  public void generateTokenUsesAuthenticatorToGenerateAJsonWebToken() {
    User user = mock(User.class);
    MockedStatic<BCrypt> bCryptMockedStatic = mockStatic(BCrypt.class);
    String username = "Pingu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);
    String encryptedPassword = "SecretNootNoot";
    String token = "SuperSecureToken";

    when(repository.findByUsername(username)).thenReturn(user);
    when(user.getPassword()).thenReturn(encryptedPassword);
    bCryptMockedStatic.when(() -> BCrypt.checkpw(password, encryptedPassword)).thenReturn(true);
    when(authenticator.generateToken(user)).thenReturn(token);

    assertEquals(token, authController.generateToken(requestBody));
    verify(repository).findByUsername(username);
    verify(user).getPassword();
    bCryptMockedStatic.verify(() -> BCrypt.checkpw(password, encryptedPassword));
    verify(authenticator).generateToken(user);

    bCryptMockedStatic.close();
  }
}
