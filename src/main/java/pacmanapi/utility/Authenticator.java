package pacmanapi.utility;

import io.jsonwebtoken.Jwts;
import pacmanapi.model.User;

import javax.crypto.SecretKey;

public class Authenticator {
  private final SecretKey secretKey;

  public Authenticator(SecretKey secretKey) {
    this.secretKey = secretKey;
  }

  public String generateJwt(User user) {
    return Jwts.builder()
            .claim("username", user.getUsername())
            .signWith(this.secretKey)
            .compact();
  }
}
