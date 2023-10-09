package pacmanapi.unit.controller;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import pacmanapi.controller.ScoresController;
import pacmanapi.model.Score;
import pacmanapi.utility.Authenticator;
import pacmanapi.utility.RedisClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    int points = 6700;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", points);
    int currentPoints = 4500;
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", username);
    String message = "Your score has been saved";
    HashMap<String, String> responseBody = new HashMap<>();
    responseBody.put("message", message);

    when(authenticator.authenticateToken(token)).thenReturn(userData);
    when(redisClient.getScore(username)).thenReturn(currentPoints);

    ResponseEntity<Object> response = scoresController.saveScore(requestHeader, requestBody);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(responseBody, response.getBody());
    verify(authenticator).authenticateToken(token);
    verify(redisClient).getScore(username);
    verify(redisClient).saveScore(username, points);
  }

  @Test
  public void saveScoreSavesWhenNoCurrentScoreExists() {
    String username = "Pingu";
    int points = 6700;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", points);
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", username);
    String message = "Your score has been saved";
    HashMap<String, String> responseBody = new HashMap<>();
    responseBody.put("message", message);

    when(authenticator.authenticateToken(token)).thenReturn(userData);
    when(redisClient.getScore(username)).thenReturn(null);

    ResponseEntity<Object> response = scoresController.saveScore(requestHeader, requestBody);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(responseBody, response.getBody());
    verify(authenticator).authenticateToken(token);
    verify(redisClient).getScore(username);
    verify(redisClient).saveScore(username, points);
  }

  @Test
  public void saveScoreDoesNotSaveScoreWhenPointsIsLowerThanCurrent() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    String username = "Pingu";
    int points = 2400;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", points);
    int currentPoints = 4500;
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", username);

    when(authenticator.authenticateToken(token)).thenReturn(userData);
    when(redisClient.getScore(username)).thenReturn(currentPoints);

    ResponseEntity<Object> response = scoresController.saveScore(requestHeader, requestBody);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(authenticator).authenticateToken(token);
    verify(redisClient).getScore(username);
    verify(redisClient, never()).saveScore(username, points);
  }

  @Test
  public void saveScoreRequiresTokenInRequestHeader() {
    HashMap<String, String> requestHeader = new HashMap<>();
    String username = "Pingu";
    int points = 2400;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", points);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> scoresController.saveScore(requestHeader, requestBody));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Missing credentials", exception.getReason());
  }

  @Test
  public void saveScoreRequiresUsernameInRequestBody() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    int points = 2400;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("points", points);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> scoresController.saveScore(requestHeader, requestBody));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Missing required key in request body", exception.getReason());
  }

  @Test
  public void saveScoreRequiresPointsInRequestBody() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    String username = "Pingu";
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> scoresController.saveScore(requestHeader, requestBody));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Missing required key in request body", exception.getReason());
  }

  @Test
  public void saveScoreThrowsErrorForInvalidToken() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    String username = "Pingu";
    int points = 2400;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", points);
    SignatureException jwtException = mock(SignatureException.class);

    when(authenticator.authenticateToken(token)).thenThrow(jwtException);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> scoresController.saveScore(requestHeader, requestBody));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid credentials", exception.getReason());
    verify(authenticator).authenticateToken(token);
  }

  @Test
  public void saveScoreThrowsErrorWhenTokenUsernameDoesNotMatchRequestBodyUsername() {
    String token = "SuperSecureToken";
    HashMap<String, String> requestHeader = new HashMap<>();
    requestHeader.put("authorization", token);
    String username = "Pingu";
    int points = 2400;
    HashMap<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("points", points);
    String wrongUsername = "ADifferentPingu";
    HashMap<String, String> userData = new HashMap<>();
    userData.put("username", wrongUsername);

    when(authenticator.authenticateToken(token)).thenReturn(userData);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> scoresController.saveScore(requestHeader, requestBody));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid credentials", exception.getReason());
    verify(authenticator).authenticateToken(token);
  }
}
