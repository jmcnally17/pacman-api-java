package pacmanapi.unit.controller;

import org.junit.jupiter.api.Test;
import pacmanapi.controller.ScoresController;
import pacmanapi.model.Score;
import pacmanapi.utility.RedisClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public final class ScoresControllerTest {
  @Test
  public void getScoresShouldReturnScoresFromRedisClientInHashMap() {
    Score score1 = mock(Score.class);
    Score score2 = mock(Score.class);
    Score score3 = mock(Score.class);
    ArrayList<Score> scores = new ArrayList<>(Arrays.asList(score1, score2, score3));
    RedisClient redisClient = mock(RedisClient.class);
    when(redisClient.getTopTenScores()).thenReturn(scores);
    ScoresController scoresController = new ScoresController(redisClient);

    HashMap<String, ArrayList<Score>> scoresData = new HashMap<>();
    scoresData.put("scores", scores);
    assertEquals(scoresData, scoresController.getScores());
    verify(redisClient).getTopTenScores();
  }
}
