package solvery.cards.controller;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.Role;
import solvery.cards.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
@Tag("integration")
public abstract class AbstractControllerTest {

  private static final Locale RU_LOCALE = new Locale("ru");

  protected static final User user1 =
      new User(1, "u1", "@@", "user1", "user1@a.ru",
          Collections.singleton(Role.USER), true);

  protected static final User user4 =
      new User(4, "au4", "@@", "advanced user 4", "auser4@a.ru",
          Set.of(Role.USER, Role.USER_ADVANCED), true);

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private MessageSourceAccessor messageSourceAccessor;

  protected String getMessage(String code) {
    return messageSourceAccessor.getMessage(code, RU_LOCALE);
  }
}
