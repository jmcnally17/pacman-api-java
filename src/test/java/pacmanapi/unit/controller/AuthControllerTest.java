package pacmanapi.unit.controller;

import org.junit.jupiter.api.AfterEach;
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
  private final UserRepository repository = mock(UserRepository.class);
  private final Authenticator authenticator = mock(Authenticator.class);
  private final User user = mock(User.class);
  private final MockedStatic<BCrypt> bCryptMockedStatic = mockStatic(BCrypt.class);

  @AfterEach
  public void afterEach() {
    reset(repository, authenticator, user);
    bCryptMockedStatic.close();
  }

  @Test
  public void generateTokenUsesAuthenticatorToGenerateAJsonWebToken() {
    when(repository.findByUsername("Pingu")).thenReturn(user);
    when(user.getPassword()).thenReturn("SecretNootNoot");
    bCryptMockedStatic.when(() -> BCrypt.checkpw("NootNoot", "SecretNootNoot")).thenReturn(true);
    when(authenticator.generateJwt(user)).thenReturn("VerySecureJwt");

    AuthController authController = new AuthController(repository, authenticator);
    assertEquals("VerySecureJwt", authController.generateToken("Pingu", "NootNoot"));
    verify(repository).findByUsername("Pingu");
    verify(user).getPassword();
    bCryptMockedStatic.verify(() -> BCrypt.checkpw("NootNoot", "SecretNootNoot"));
    verify(authenticator).generateJwt(user);
  }
}
