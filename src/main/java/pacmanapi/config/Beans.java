package pacmanapi.config;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pacmanapi.model.User;
import pacmanapi.utility.Authenticator;
import pacmanapi.utility.RedisClient;
import redis.clients.jedis.JedisPooled;

import javax.crypto.SecretKey;

@Configuration
public class Beans {
  @Value("${redis.host}")
  private String redisHost;
  @Value("${redis.port}")
  private int redisPort;
  @Value("${redis.user}")
  private String redisUser;
  @Value("${redis.password}")
  private String redisPassword;

  @Bean
  public JedisPooled jedis() {
    return new JedisPooled(this.redisHost, this.redisPort, this.redisUser, this.redisPassword);
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
