package pacmanapi.unit.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacmanapi.models.Score;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreTest {
  private Score score;

  @BeforeEach
  public void beforeEach() {
    score = new Score("Alan", 666);
  }

  @Test
  public void hasName() {
    assertEquals("Alan", score.name());
  }

  @Test
  public void hasPoints() {
    assertEquals(666, score.points());
  }
}
