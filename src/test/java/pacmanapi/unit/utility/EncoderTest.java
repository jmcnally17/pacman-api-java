package pacmanapi.unit.utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pacmanapi.utility.Encoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EncoderTest {
  private final MockedStatic<BCrypt> bCrypt = mockStatic(BCrypt.class);

  @BeforeEach
  public void beforeEach() {
    bCrypt.when(BCrypt::gensalt).thenReturn("SaltySalt");
  }

  @AfterEach
  public void afterEach() {
    bCrypt.close();
  }

  @Test
  public void usesBCryptToGenerateASaltUponInstantiation() {
    Encoder encoder = new Encoder();
    assertEquals("SaltySalt", encoder.getSalt());
    bCrypt.verify(BCrypt::gensalt);
  }

  @Test
  public void usesBCryptToHashAPassword() {
    bCrypt.when(() -> BCrypt.hashpw("NootNoot", "SaltySalt")).thenReturn("SecretNootNoot");
    Encoder encoder = new Encoder();
    assertEquals("SecretNootNoot", encoder.encodePassword("NootNoot"));
    bCrypt.verify(() -> BCrypt.hashpw("NootNoot", "SaltySalt"));
  }
}
