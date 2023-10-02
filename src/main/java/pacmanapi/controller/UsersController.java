package pacmanapi.controller;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import pacmanapi.model.User;
import pacmanapi.repository.UserRepository;

import java.util.HashMap;

@RestController
public class UsersController {
  private final UserRepository repository;

  public UsersController(UserRepository repository) {
    this.repository = repository;
  }

  @CrossOrigin(origins = "http://localhost:8000")
  @GetMapping("/users/{username}")
  public HashMap<String, HashMap<String, String>> getUser(@PathVariable String username) {
    User user = this.repository.findByUsername(username);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", user.getUsername());
    HashMap<String, HashMap<String, String>> responseData = new HashMap<>();
    responseData.put("user", userData);

    return responseData;
  }

  @CrossOrigin(origins = "http://localhost:8000")
  @PostMapping("users")
  public void createUser(@RequestParam String username, @RequestParam String password, User user) {
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    user.setUsername(username);
    user.setPassword(hashedPassword);
    this.repository.save(user);
  }
}
