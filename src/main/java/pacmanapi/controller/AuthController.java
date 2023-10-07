package pacmanapi.controller;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;
import pacmanapi.utility.Authenticator;

import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class AuthController {
  private final UserRepository repository;
  private final Authenticator authenticator;

  public AuthController(UserRepository repository, Authenticator authenticator) {
    this.repository = repository;
    this.authenticator = authenticator;
  }

  @GetMapping("/auth")
  public HashMap<String, HashMap<String, String>> authenticateToken(@RequestHeader HashMap<String, String> header) {
    String token = header.get("authorization");
    HashMap<String, String> userData = authenticator.authenticateToken(token);
    HashMap<String, HashMap<String, String>> responseData = new HashMap<>();
    responseData.put("user", userData);
    return responseData;
  }

  @PostMapping("/auth")
  public String generateToken(@RequestBody HashMap<String, String> body) {
    User foundUser = repository.findByUsername(body.get("username"));
    if (BCrypt.checkpw(body.get("password"), foundUser.getPassword())) {
      return authenticator.generateToken(foundUser);
    }
    return "";
  }
}
