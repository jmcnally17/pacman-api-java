package pacmanapi.config;

import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pacmanapi.model.User;
import pacmanapi.utility.Authenticator;
import pacmanapi.utility.RedisClient;
import redis.clients.jedis.JedisPooled;

import javax.crypto.SecretKey;

@Configuration
public class Beans {
  @Bean
  public JedisPooled jedis() {
    return new JedisPooled("localhost", 6379);
  }

  @Bean
  public RedisClient redisClient(JedisPooled jedis) {
    return new RedisClient(jedis);
  }

  @Bean
  public User user() {
    return new User();
  }

  @Bean
  public SecretKey secretKey() {
    return Jwts.SIG.HS256.key().build();
  }

  @Bean
  public Authenticator authenticator(SecretKey secretKey) {
    return new Authenticator(secretKey);
  }
}
