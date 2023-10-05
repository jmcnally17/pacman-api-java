package pacmanapi.controller;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;
import pacmanapi.utility.Authenticator;

@RestController
public class AuthController {
  private final UserRepository repository;
  private final Authenticator authenticator;

  public AuthController(UserRepository repository, Authenticator authenticator) {
    this.repository = repository;
    this.authenticator = authenticator;
  }

  @CrossOrigin(origins = "http://localhost:8000")
  @PostMapping("/auth")
  public String generateToken(@RequestParam String username, @RequestParam String password) {
    User foundUser = repository.findByUsername(username);
    if (BCrypt.checkpw(password, foundUser.getPassword())) {
      return authenticator.generateJwt(foundUser);
    }
    return "";
  }
}
