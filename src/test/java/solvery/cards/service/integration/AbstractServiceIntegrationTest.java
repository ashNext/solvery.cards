package solvery.cards.service.integration;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Tag("integration")
public abstract class AbstractServiceIntegrationTest {

  @Autowired
  public MessageSourceAccessor messageSourceAccessor;
}
