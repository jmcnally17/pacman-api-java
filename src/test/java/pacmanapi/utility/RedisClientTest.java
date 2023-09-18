package pacmanapi.utility;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pacmanapi.models.Score;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@Test
public class RedisClientTest {
  private JedisPooled jedis;
  private RedisClient redisClient;

  @BeforeMethod
  public void beforeTest() {
    jedis = mock(JedisPooled.class);
    redisClient = new RedisClient(jedis);
  }

  @Test
  public void formatsScoresForClient() {
    this.setUpMockData();
    Score score1 = new Score("Alan", 10000);
    Score score2 = new Score("Steve", 5500);
    ArrayList<Score> scores = new ArrayList<>();
    scores.add(score1);
    scores.add(score2);

    assertEquals(redisClient.getTopTenScores(), scores, "Formatted scores should be the same as the expected ArrayList");
    verify(jedis).zrevrangeWithScores("scores", 0, 10);
  }

  private void setUpMockData() {
    Tuple scoreData1 = new Tuple("Alan", 10000.0);
    Tuple scoreData2 = new Tuple("Steve", 5500.0);
    ArrayList<Tuple> data = new ArrayList<>();
    data.add(scoreData1);
    data.add(scoreData2);
    when(jedis.zrevrangeWithScores("scores", 0, 10)).thenReturn(data);
  }
}
