package pacmanapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    if (token == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing token in request header");
    }
    HashMap<String, String> userData;
    try {
      userData = authenticator.authenticateToken(token);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    }
    HashMap<String, HashMap<String, String>> responseData = new HashMap<>();
    responseData.put("user", userData);
    return responseData;
  }

  @PostMapping("/auth")
  public String generateToken(@RequestBody HashMap<String, String> body) throws ResponseStatusException {
    String username = body.get("username");
    String password = body.get("password");
    if (username == null || password == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required key in request body");
    }
    User foundUser = repository.findByUsername(username);
    if (foundUser == null || !BCrypt.checkpw(password, foundUser.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    return authenticator.generateToken(foundUser);
  }
}
