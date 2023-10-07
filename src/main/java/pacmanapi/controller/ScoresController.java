package pacmanapi.controller;

import org.springframework.web.bind.annotation.*;
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
  public void saveScore(@RequestHeader HashMap<String, String> header, @RequestBody HashMap<String, Object> body) {
    HashMap<String, String> tokenUserData = this.authenticator.authenticateToken(header.get("authorization"));
    String username = tokenUserData.get("username");
    if (username.equals(body.get("username"))) {
      Integer currentScore = this.redisClient.getScore(username);
      int newPoints = (int) body.get("points");
      if (currentScore == null || newPoints > currentScore) {
        this.redisClient.saveScore(username, newPoints);
      }
    }
  }
}
