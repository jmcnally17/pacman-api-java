import static org.testng.Assert.*;
import org.testng.annotations.Test;

@Test
public class MainTest {
  @Test
  public void checkAddition() {
    assertSame(4, 2 + 2, "2 + 2 should equal 4");
  }
}
