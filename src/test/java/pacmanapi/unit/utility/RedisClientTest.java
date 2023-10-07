package pacmanapi.unit.utility;

import org.junit.jupiter.api.Test;
import pacmanapi.model.Score;
import pacmanapi.utility.RedisClient;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RedisClientTest {
  @Test
  public void formatsScores() {
    JedisPooled jedis = mock(JedisPooled.class);
    this.setUpMockData(jedis);
    Score score1 = new Score("Alan", 10000);
    Score score2 = new Score("Steve", 5500);
    ArrayList<Score> scores = new ArrayList<>();
    scores.add(score1);
    scores.add(score2);

    RedisClient redisClient = new RedisClient(jedis);
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
