package pacmanapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;

import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class UsersController {
  private final UserRepository repository;

  public UsersController(UserRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/users/{username}")
  public HashMap<String, HashMap<String, String>> getUser(@PathVariable String username) {
    User user = this.repository.findByUsername(username);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", user.getUsername());
    HashMap<String, HashMap<String, String>> responseData = new HashMap<>();
    responseData.put("user", userData);

    return responseData;
  }

  @PostMapping("/users")
  public void createUser(@RequestBody HashMap<String, String> body, User user) throws ResponseStatusException {
    String username = body.get("username");
    String password = body.get("password");
    if (username == null || password == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required key in request body");
    }
    if (username.contains(" ")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username cannot contain any spaces");
    } else if (username.length() < 3 || username.length() > 15) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username must be 3-15 characters long");
    } else if (password.length() < 8) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password must be at least 8 characters long");
    }
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    user.setUsername(body.get("username"));
    user.setPassword(hashedPassword);
    try {
      this.repository.save(user);
    } catch (Exception e) {
      String message = e.getMessage();
      if (message.contains("code=11000")) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
      } else {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
      }
    }
  }
}
