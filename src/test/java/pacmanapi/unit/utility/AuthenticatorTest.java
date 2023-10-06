package pacmanapi.unit.utility;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pacmanapi.model.User;
import pacmanapi.utility.Authenticator;

import javax.crypto.SecretKey;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticatorTest {
  private final SecretKey secretKey = mock(SecretKey.class);
  private final MockedStatic<Jwts> jwtsMockedStatic = mockStatic(Jwts.class);
  private Authenticator authenticator;

  @BeforeEach
  public void beforeEach() {
    authenticator = new Authenticator(secretKey);
  }

  @AfterEach
  public void afterEach() {
    reset(secretKey);
    jwtsMockedStatic.close();
  }

  @Test
  public void generateTokenCreatesAJwt() {
    User user = mock(User.class);
    JwtBuilder builder = mock(JwtBuilder.class);
    String token = "SuperSecureToken";

    jwtsMockedStatic.when(Jwts::builder).thenReturn(builder);
    when(user.getUsername()).thenReturn("Pingu");
    when(builder.claim("username", "Pingu")).thenReturn(builder);
    when(builder.signWith(secretKey)).thenReturn(builder);
    when(builder.compact()).thenReturn(token);

    assertEquals(token, authenticator.generateToken(user));
    jwtsMockedStatic.verify(Jwts::builder);
    verify(builder).claim("username", "Pingu");
    verify(builder).signWith(secretKey);
    verify(builder).compact();
  }

  @Test
  public void decodeTokenReadsATokenWithDoReturn() {
    JwtParserBuilder parserBuilder = mock(JwtParserBuilder.class);
    JwtParser parser = mock(JwtParser.class);
    Jwt<?, ?> jwt = mock(Jwt.class);
    Claims claims = mock(Claims.class);
    String token = "SuperSecureToken";
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", "Pingu");

    jwtsMockedStatic.when(Jwts::parser).thenReturn(parserBuilder);
    when(parserBuilder.verifyWith(secretKey)).thenReturn(parserBuilder);
    when(parserBuilder.build()).thenReturn(parser);
    doReturn(jwt).when(parser).parse(token);
    doReturn(claims).when(jwt).getPayload();
    when(claims.get("username")).thenReturn("Pingu");

    assertEquals(userData, authenticator.decodeToken(token));
    jwtsMockedStatic.verify(Jwts::parser);
    verify(parserBuilder).verifyWith(secretKey);
    verify(parserBuilder).build();
    verify(parser).parse(token);
    verify(jwt).getPayload();
    verify(claims).get("username");
  }

  @Test
  public void decodeTokenReadsATokenWithAnswer() {
    JwtParserBuilder parserBuilder = mock(JwtParserBuilder.class);
    JwtParser parser = mock(JwtParser.class);
    Jwt<?, ?> jwt = mock(Jwt.class);
    Claims claims = mock(Claims.class);
    String token = "SuperSecureToken";
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", "Pingu");

    jwtsMockedStatic.when(Jwts::parser).thenReturn(parserBuilder);
    when(parserBuilder.verifyWith(secretKey)).thenReturn(parserBuilder);
    when(parserBuilder.build()).thenReturn(parser);
    when(parser.parse(token)).thenAnswer(invocation -> jwt);
    when(jwt.getPayload()).thenAnswer(invocation -> claims);
    when(claims.get("username")).thenReturn("Pingu");

    assertEquals(userData, authenticator.decodeToken(token));
    jwtsMockedStatic.verify(Jwts::parser);
    verify(parserBuilder).verifyWith(secretKey);
    verify(parserBuilder).build();
    verify(parser).parse(token);
    verify(jwt).getPayload();
    verify(claims).get("username");
  }
}
