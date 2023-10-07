package pacmanapi.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import pacmanapi.model.User;

import javax.crypto.SecretKey;
import java.util.HashMap;

public class Authenticator {
  private final SecretKey secretKey;

  public Authenticator(SecretKey secretKey) {
    this.secretKey = secretKey;
  }

  public String generateToken(User user) {
    return Jwts.builder()
            .claim("username", user.getUsername())
            .signWith(this.secretKey)
            .compact();
  }

  public HashMap<String, String> authenticateToken(String token) {
    Jwt<?, ?> jwt = Jwts.parser()
            .verifyWith(this.secretKey)
            .build()
            .parse(token);
    Claims claims = (Claims) jwt.getPayload();
    String username = (String) claims.get("username");
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", username);
    return userData;
  }
}
