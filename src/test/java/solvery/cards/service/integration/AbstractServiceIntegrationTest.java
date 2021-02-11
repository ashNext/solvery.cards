package solvery.cards.service.integration;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Tag("integration")
public abstract class AbstractServiceIntegrationTest {
}
