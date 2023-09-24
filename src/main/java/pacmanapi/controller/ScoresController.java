package pacmanapi.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pacmanapi.model.Score;
import pacmanapi.utility.RedisClient;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public final class ScoresController {
  private final RedisClient redisClient;

  public ScoresController(RedisClient redisClient) {
    this.redisClient = redisClient;
  }

  @CrossOrigin(origins = "http://localhost:8000")
  @GetMapping("/scores")
  public HashMap<String, ArrayList<Score>> getScores() {
    ArrayList<Score> scores = this.redisClient.getTopTenScores();
    HashMap<String, ArrayList<Score>> responseData = new HashMap<>();
    responseData.put("scores", scores);
    return responseData;
  }
}
