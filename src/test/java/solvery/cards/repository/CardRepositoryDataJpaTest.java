package solvery.cards.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import solvery.cards.model.Card;
import solvery.cards.model.Role;
import solvery.cards.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql",
    "/create-cards-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-cards-after.sql",
    "/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Tag("slow")
class CardRepositoryDataJpaTest {

  @Autowired
  private CardRepository repository;

  private final User user =
      new User(1, "u1", "@@", "user1", "user1@a.ru", Collections.singleton(Role.USER), true);

  private final static User advancedUser =
      new User(4, "au4", "1", "advanced user 4", "auser4@a.ru",
          Set.of(Role.USER, Role.USER_ADVANCED), true);

  private final List<Card> exceptedEnabledCards = List.of(
      new Card(1, user, "11", 0, true),
      new Card(2, user, "12", 0, true),
      new Card(3, user, "13", 0, true)
  );

  private final List<Card> exceptedAllCards = List.of(
      new Card(9, advancedUser, "41", 0, true),
      new Card(10, advancedUser, "42", 0, false),
      new Card(11, advancedUser, "43", 0, true),
      new Card(12, advancedUser, "44", 0, false)
  );

  @Test
  void getAllByUserAndEnabled() {
    List<Card> actualCards = repository.getAllByUserAndEnabled(user, true);
    assertThat(exceptedEnabledCards).usingRecursiveComparison().ignoringFields("user.password")
        .isEqualTo(actualCards);
  }

  @Test
  void getAllByUser() {
    List<Card> actualCards = repository.getAllByUser(advancedUser);
    assertThat(exceptedAllCards).usingRecursiveComparison().ignoringFields("user.password")
        .isEqualTo(actualCards);
  }
}