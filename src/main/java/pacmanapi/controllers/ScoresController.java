package pacmanapi.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pacmanapi.models.Score;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ScoresController {
  @CrossOrigin(origins = "http://localhost:8000")
  @GetMapping("/scores")
  public HashMap<String, ArrayList<Score>> getScores() {
    JedisPooled jedis = new JedisPooled("localhost", 6379);
    List<Tuple> scoresData = jedis.zrevrangeWithScores("scores", 0, 10);

    ArrayList<Score> scores = new ArrayList<>();
    for (Tuple score : scoresData) {
      scores.add(new Score(score.getElement(), ((int) score.getScore())));
    }

    HashMap<String, ArrayList<Score>> responseData = new HashMap<>();
    responseData.put("scores", scores);

    return responseData;
  }
}
