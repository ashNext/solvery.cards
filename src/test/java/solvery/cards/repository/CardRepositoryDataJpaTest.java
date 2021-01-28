package solvery.cards.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import solvery.cards.model.Card;
import solvery.cards.model.Role;
import solvery.cards.model.User;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql", "/create-cards-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-cards-after.sql", "/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CardRepositoryDataJpaTest {

  @Autowired
  private CardRepository repository;

  private final User user =
      new User(1, "u1", "@@", "user1", "a@b.ru", Collections.singleton(Role.USER));

  private final List<Card> exceptedEnabledCards = List.of(
      new Card(1, user, "11", 0, true),
      new Card(2, user, "12", 0, true),
      new Card(3, user, "13", 0, true)
  );

  @Test
  void getAllByUser() {
    List<Card> actualCards = repository.getAllByUser(user, true);
    assertThat(exceptedEnabledCards).usingRecursiveComparison().ignoringFields("user.password").isEqualTo(actualCards);
  }
}