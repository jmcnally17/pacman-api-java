package pacmanapi.utility;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Encoder {
  private final String salt;

  public Encoder() {
    salt = BCrypt.gensalt();
  }

  public String getSalt() {
    return this.salt;
  }

  public String encodePassword(String password) {
    return BCrypt.hashpw(password, this.salt);
  }
}
