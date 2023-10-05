package pacmanapi.unit.utility;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedStatic;
import pacmanapi.model.User;
import pacmanapi.utility.Authenticator;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticatorTest {
  private final SecretKey secretKey = mock(SecretKey.class);
  private final MockedStatic<Jwts> jwtsMockedStatic = mockStatic(Jwts.class);
  private final User user = mock(User.class);
  private final JwtBuilder builder = mock(JwtBuilder.class);

  @AfterEach
  public void afterEach() {
    reset(secretKey, user, builder);
    jwtsMockedStatic.close();
  }

  public void generateJwtCreatesAJwt() {
    jwtsMockedStatic.when(Jwts::builder).thenReturn(builder);
    when(user.getUsername()).thenReturn("Pingu");
    when(builder.claim("username", "Pingu")).thenReturn(builder);
    when(builder.signWith(secretKey)).thenReturn(builder);
    when(builder.compact()).thenReturn("NotSoSecretKey");

    Authenticator authenticator = new Authenticator(secretKey);
    assertEquals("NotSoSecretKey", authenticator.generateJwt(user));
    jwtsMockedStatic.verify(Jwts::builder);
    verify(builder).claim("username", "Pingu");
    verify(builder).signWith(secretKey);
    verify(builder).compact();
  }
}
