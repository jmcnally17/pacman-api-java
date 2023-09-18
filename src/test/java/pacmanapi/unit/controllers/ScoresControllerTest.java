package pacmanapi.unit.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pacmanapi.controllers.ScoresController;
import pacmanapi.models.Score;
import pacmanapi.utility.RedisClient;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ScoresController.class)
@Import(RedisClient.class)
public final class ScoresControllerTest {
  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private RedisClient redisClient;

  @Test
  public void getScoresShouldReturnFormattedScoresFromRedisClient() {
    ArrayList<Score> scores = new ArrayList<>();
    scores.add(new Score("OceanFinance", 12000));
    scores.add(new Score("Alan", 6500));
    scores.add(new Score("Steve", 1530));
    when(redisClient.getTopTenScores()).thenReturn(scores);
    String scoresJson = "{\"scores\":[{\"name\":\"OceanFinance\",\"points\":12000},{\"name\":\"Alan\",\"points\":6500},{\"name\":\"Steve\",\"points\":1530}]}";

    webTestClient.get()
            .uri("/scores")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(String.class).isEqualTo(scoresJson);
    verify(redisClient).getTopTenScores();
  }
}
