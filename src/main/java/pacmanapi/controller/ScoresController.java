package pacmanapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pacmanapi.model.Score;
import pacmanapi.utility.Authenticator;
import pacmanapi.utility.RedisClient;

import java.util.ArrayList;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class ScoresController {
  private final RedisClient redisClient;
  private final Authenticator authenticator;

  public ScoresController(RedisClient redisClient, Authenticator authenticator) {
    this.redisClient = redisClient;
    this.authenticator = authenticator;
  }

  @GetMapping("/scores")
  public HashMap<String, ArrayList<Score>> getScores() {
    ArrayList<Score> scores = this.redisClient.getTopTenScores();
    HashMap<String, ArrayList<Score>> responseData = new HashMap<>();
    responseData.put("scores", scores);
    return responseData;
  }

  @PostMapping("/scores")
  @ResponseBody
  public ResponseEntity<Object> saveScore(@RequestHeader HashMap<String, String> header, @RequestBody HashMap<String, Object> body) throws ResponseStatusException {
    String token = header.get("authorization");
    String username = (String) body.get("username");
    Integer points = (Integer) body.get("points");
    if (token == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing credentials");
    } else if (username == null || points == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required key in request body");
    }
    HashMap<String, String> tokenUserData;
    try {
      tokenUserData = this.authenticator.authenticateToken(token);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    if (!username.equals(tokenUserData.get("username"))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    Integer currentScore = this.redisClient.getScore(username);
    if (currentScore == null || points > currentScore) {
      this.redisClient.saveScore(username, points);
      String message = "Your score has been saved";
      HashMap<String, String> responseBody = new HashMap<>();
      responseBody.put("message", message);
      return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }
}
