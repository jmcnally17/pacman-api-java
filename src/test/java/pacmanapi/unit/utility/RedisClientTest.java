package pacmanapi.unit.utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacmanapi.model.Score;
import pacmanapi.utility.RedisClient;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RedisClientTest {
  private final JedisPooled jedis = mock(JedisPooled.class);
  private RedisClient redisClient;

  @BeforeEach
  public void beforeEach() {
    redisClient = new RedisClient(jedis);
  }

  @AfterEach
  public void afterEach() {
    reset(jedis);
  }

  @Test
  public void findsAScoreByName() {
    String key = "scores";
    String username = "Pingu";
    int points = 4500;

    when(jedis.zscore(key, username)).thenReturn(4500.0);

    assertEquals(points, redisClient.getScore(username));
    verify(jedis).zscore(key, username);
  }

  @Test
  public void savesAScore() {
    String key = "scores";
    String username = "Pingu";
    int points = 3600;

    redisClient.saveScore(username, points);
    verify(jedis).zadd(key, points, username);
  }

  @Test
  public void getsTheTopTenScoresInRedis() {
    this.setUpMockData(jedis);
    Score score1 = new Score("Alan", 10000);
    Score score2 = new Score("Steve", 5500);
    ArrayList<Score> scores = new ArrayList<>();
    scores.add(score1);
    scores.add(score2);

    assertEquals(redisClient.getTopTenScores(), scores, "Formatted scores should be the same as the expected ArrayList");
    verify(jedis).zrevrangeWithScores("scores", 0, 10);
  }

  private void setUpMockData(JedisPooled jedis) {
    Tuple scoreData1 = new Tuple("Alan", 10000.0);
    Tuple scoreData2 = new Tuple("Steve", 5500.0);
    ArrayList<Tuple> data = new ArrayList<>();
    data.add(scoreData1);
    data.add(scoreData2);
    when(jedis.zrevrangeWithScores("scores", 0, 10)).thenReturn(data);
  }
}
