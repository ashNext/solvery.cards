package solvery.cards.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import solvery.cards.model.Card;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.CardService;
import solvery.cards.service.CardServiceInterfaceTest;
import solvery.cards.util.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Sql(
    value = {"/create-users-before.sql", "/create-cards-before.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/create-cards-after.sql", "/create-users-after.sql"},
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CardServiceIntegrationTest extends AbstractServiceIntegrationTest implements CardServiceInterfaceTest {

  @Autowired
  private CardService service;

  private final static User user =
      new User(1, "u1", "1", "user1", "user1@a.ru",
          Collections.singleton(Role.USER), true);

  @Test
  @Override
  public void getAllEnabledByUser() {
    List<Card> exceptedCards = List.of(
        new Card(1, user, "11", 0, true),
        new Card(2, user, "12", 0, true),
        new Card(3, user, "13", 0, true)
    );

    List<Card> actualCards = service.getAllEnabledByUser(user);
    assertThat(actualCards).usingElementComparatorIgnoringFields("user").isEqualTo(exceptedCards);
  }

  @Test
  @Override
  public void create() {
    Card exceptedCard = new Card(9, user, "14", 0, true);
    Card actualCard = service.create(new Card(user, "14"));

    assertThat(actualCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
    assertThat(service.getEnabledById(9)).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void createShouldReturnDuplicateNumber() {
    Card newCard = new Card(user, "11");

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> service.create(newCard))
        .havingRootCause()
        .withMessageContaining("cards_unique_numb_idx");
  }

  @Test
  @Override
  public void close() {
    Card exceptedCard = new Card(2, user, "12", 0, false);
    Card closedCard = service.close(2);
    assertThat(closedCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getEnabledById() {
    Card exceptedCard = new Card(1, user, "11", 0, true);
    Card actualCard = service.getEnabledById(1);
    assertThat(actualCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getEnabledByIdShouldReturnNotFound() {
    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getEnabledById(9));
    assertEquals(messageSourceAccessor.getMessage("card.cardByIdNotFound"), exception.getMessage());
  }

  @Test
  @Override
  public void getEnabledByCardNumb() {
    Card exceptedCard = new Card(1, user, "11", 0, true);
    Card actualCard = service.getEnabledByCardNumb("11");
    assertThat(actualCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getEnabledByCardNumbShouldReturnNotFound() {
    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getEnabledByCardNumb("14"));
    assertEquals(messageSourceAccessor.getMessage("card.numbNotFound", new Object[]{"14"}), exception.getMessage());
  }
}
