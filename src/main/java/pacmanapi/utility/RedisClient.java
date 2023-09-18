package pacmanapi.utility;

import pacmanapi.models.Score;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

public final class RedisClient {
  private final JedisPooled jedis;

  public RedisClient(JedisPooled jedis) {
    this.jedis = jedis;
  }

  public ArrayList<Score> getTopTenScores() {
    List<Tuple> scoresData = this.getSortedSetData("scores", 0, 10);
    return this.convertScoresData(scoresData);
  }

  private List<Tuple> getSortedSetData(String key, int start, int stop) {
    return this.jedis.zrevrangeWithScores(key, start, stop);
  }

  private ArrayList<Score> convertScoresData(List<Tuple> scoresData) {
    ArrayList<Score> scores = new ArrayList<>();
    for (Tuple score : scoresData) {
      scores.add(new Score(score.getElement(), ((int) score.getScore())));
    }
    return scores;
  }
}
