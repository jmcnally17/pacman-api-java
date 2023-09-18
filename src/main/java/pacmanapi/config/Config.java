package pacmanapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pacmanapi.utility.RedisClient;
import redis.clients.jedis.JedisPooled;

@Configuration
public class Config {
  @Bean
  public JedisPooled jedis() {
    return new JedisPooled("localhost", 6379);
  }

  @Bean
  public RedisClient redisClient(JedisPooled jedis) {
    return new RedisClient(jedis);
  }
}
