package pacmanapi.unit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacmanapi.model.Score;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreTest {
  private Score score;

  @BeforeEach
  public void beforeEach() {
    score = new Score("Alan", 666);
  }

  @Test
  public void hasUsername() {
    assertEquals("Alan", score.username());
  }

  @Test
  public void hasPoints() {
    assertEquals(666, score.points());
  }
}
