package pacmanapi.unit.controller;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pacmanapi.controller.AuthController;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;
import pacmanapi.utility.Authenticator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
  @Test
  public void generateTokenUsesAuthenticatorToGenerateAJsonWebToken() {
    UserRepository repository = mock(UserRepository.class);
    Authenticator authenticator = mock(Authenticator.class);
    User user = mock(User.class);
    MockedStatic<BCrypt> bCryptMockedStatic = mockStatic(BCrypt.class);
    String username = "Pingu";
    String password = "NootNoot";
    String encryptedPassword = "SecretNootNoot";
    String token = "SuperSecureToken";

    when(repository.findByUsername(username)).thenReturn(user);
    when(user.getPassword()).thenReturn(encryptedPassword);
    bCryptMockedStatic.when(() -> BCrypt.checkpw(password, encryptedPassword)).thenReturn(true);
    when(authenticator.generateToken(user)).thenReturn(token);

    AuthController authController = new AuthController(repository, authenticator);
    assertEquals(token, authController.generateToken(username, password));
    verify(repository).findByUsername(username);
    verify(user).getPassword();
    bCryptMockedStatic.verify(() -> BCrypt.checkpw(password, encryptedPassword));
    verify(authenticator).generateToken(user);

    bCryptMockedStatic.close();
  }
}
