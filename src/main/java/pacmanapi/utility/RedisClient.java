package pacmanapi.utility;

import pacmanapi.model.Score;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

public final class RedisClient {
  private final String key = "scores";
  private final JedisPooled jedis;

  public RedisClient(JedisPooled jedis) {
    this.jedis = jedis;
  }

  public Integer getScore(String username) {
    Double foundScore = this.jedis.zscore(this.key, username);
    if (foundScore == null) {
      return null;
    }
    return foundScore.intValue();
  }

  public void saveScore(String username, int points) {
    this.jedis.zadd(this.key, points, username);
  }

  public ArrayList<Score> getTopTenScores() {
    List<Tuple> scoresData = this.jedis.zrevrangeWithScores(this.key, 0, 10);
    return this.convertScoresData(scoresData);
  }

  private ArrayList<Score> convertScoresData(List<Tuple> scoresData) {
    ArrayList<Score> scores = new ArrayList<>();
    for (Tuple score : scoresData) {
      scores.add(new Score(score.getElement(), (int) score.getScore()));
    }
    return scores;
  }
}
