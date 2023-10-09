package pacmanapi.unit.controller;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.server.ResponseStatusException;
import pacmanapi.controller.AuthController;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;
import pacmanapi.utility.Authenticator;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
  private final User user = mock(User.class);
  private final MockedStatic<BCrypt> bCryptMockedStatic = mockStatic(BCrypt.class);
  private final UserRepository repository = mock(UserRepository.class);
  private final Authenticator authenticator = mock(Authenticator.class);
  private AuthController authController;

  @BeforeEach
  public void beforeEach() {
    authController = new AuthController(repository, authenticator);
  }

  @AfterEach
  public void afterEach() {
    reset(user, repository, authenticator);
    bCryptMockedStatic.close();
  }

  @Test
  public void authenticateTokenUsesAuthenticatorToAuthenticateAJsonWebToken() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", "Pingu");
    HashMap<String, HashMap<String, String>> responseData = new HashMap<>();
    responseData.put("user", userData);

    when(authenticator.authenticateToken(token)).thenReturn(userData);

    assertEquals(responseData, authController.authenticateToken(requestHeader));
    verify(authenticator).authenticateToken(token);
  }

  @Test
  public void authenticateTokenThrowsErrorIfNoAuthorizationInHeader() {
    HashMap<String, String> requestHeader = new HashMap<>();

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.authenticateToken(requestHeader));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Missing token in request header", exception.getReason());
  }

  @Test
  public void authenticateTokenThrowsErrorTokenIsInvalid() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    SignatureException jwtException = mock(SignatureException.class);

    when(authenticator.authenticateToken(token)).thenThrow(jwtException);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.authenticateToken(requestHeader));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid token", exception.getReason());
    verify(authenticator).authenticateToken(token);
  }

  @Test
  public void generateTokenUsesAuthenticatorToGenerateAJsonWebToken() {
    User user = mock(User.class);
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
  }

  @Test
  public void generateTokenRequiresUsernameInRequestBody() {
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("password", password);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.generateToken(requestBody));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Missing required key in request body", exception.getReason());
  }

  @Test
  public void generateTokenRequiresPasswordInRequestBody() {
    String username = "Pingu";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.generateToken(requestBody));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Missing required key in request body", exception.getReason());
  }

  @Test
  public void generateTokenThrowsErrorForInvalidUsername() {
    String username = "Pingu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);

    when(repository.findByUsername(username)).thenReturn(null);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.generateToken(requestBody));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid credentials", exception.getReason());
    verify(repository).findByUsername(username);
  }

  @Test
  public void generateTokenThrowsErrorForInvalidPassword() {
    String username = "Pingu";
    String password = "NootNoot";
    HashMap<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("password", password);
    String encryptedPassword = "SecretNootNoot";

    when(repository.findByUsername(username)).thenReturn(user);
    when(user.getPassword()).thenReturn(encryptedPassword);
    bCryptMockedStatic.when(() -> BCrypt.checkpw(password, encryptedPassword)).thenReturn(false);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authController.generateToken(requestBody));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid credentials", exception.getReason());
    verify(repository).findByUsername(username);
    verify(user).getPassword();
    bCryptMockedStatic.verify(() -> BCrypt.checkpw(password, encryptedPassword));
  }
}
