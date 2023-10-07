package pacmanapi.unit.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacmanapi.controller.ScoresController;
import pacmanapi.model.Score;
import pacmanapi.utility.Authenticator;
import pacmanapi.utility.RedisClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public final class ScoresControllerTest {
  private final RedisClient redisClient = mock(RedisClient.class);
  private final Authenticator authenticator = mock(Authenticator.class);
  private ScoresController scoresController;

  @BeforeEach
  public void beforeEach() {
    scoresController = new ScoresController(redisClient, authenticator);
  }

  @AfterEach
  public void afterEach() {
    reset(redisClient, authenticator);
  }

  @Test
  public void getScoresShouldReturnScoresFromRedisClientInHashMap() {
    Score score1 = mock(Score.class);
    Score score2 = mock(Score.class);
    Score score3 = mock(Score.class);
    ArrayList<Score> scores = new ArrayList<>(Arrays.asList(score1, score2, score3));
    HashMap<String, ArrayList<Score>> scoresData = new HashMap<>();
    scoresData.put("scores", scores);

    when(redisClient.getTopTenScores()).thenReturn(scores);

    assertEquals(scoresData, scoresController.getScores());
    verify(redisClient).getTopTenScores();
  }

  @Test
  public void saveScoreUsesAuthenticatorToSaveScoreForUser() {
    String username = "Pingu";
    int newPoints = 6700;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", newPoints);
    int currentPoints = 4500;
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", username);

    when(authenticator.authenticateToken(token)).thenReturn(userData);
    when(redisClient.getScore(username)).thenReturn(currentPoints);

    scoresController.saveScore(requestHeader, requestBody);
    verify(authenticator).authenticateToken(token);
    verify(redisClient).getScore(username);
    verify(redisClient).saveScore(username, newPoints);
  }

  @Test
  public void saveScoreSavesWhenNoCurrentScoreExists() {
    String username = "Pingu";
    int newPoints = 6700;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", newPoints);
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", username);

    when(authenticator.authenticateToken(token)).thenReturn(userData);
    when(redisClient.getScore(username)).thenReturn(null);

    scoresController.saveScore(requestHeader, requestBody);
    verify(authenticator).authenticateToken(token);
    verify(redisClient).getScore(username);
    verify(redisClient).saveScore(username, newPoints);
  }

  @Test
  public void doesNotSaveScoreWhenNewPointsIsLowerThanCurrent() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    String username = "Pingu";
    int newPoints = 2400;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", newPoints);
    int currentPoints = 4500;
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", username);

    when(authenticator.authenticateToken(token)).thenReturn(userData);
    when(redisClient.getScore(username)).thenReturn(currentPoints);

    scoresController.saveScore(requestHeader, requestBody);
    verify(authenticator).authenticateToken(token);
    verify(redisClient).getScore(username);
    verify(redisClient, never()).saveScore(username, newPoints);
  }
}
