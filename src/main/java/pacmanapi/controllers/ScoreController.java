package pacmanapi.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pacmanapi.models.Score;

import java.util.HashMap;

@RestController
public class ScoreController {
  @CrossOrigin(origins = "http://localhost:8000")
  @GetMapping("/scores")
  public HashMap<String, Score[]> getScores() {
    Score score1 = new Score("Joe", 15000);
    Score score2 = new Score("OceanFinance", 5000);
    Score[] scores = {score1, score2};
    HashMap<String, Score[]> responseData = new HashMap<>();
    responseData.put("scores", scores);
    return responseData;
  }
}
