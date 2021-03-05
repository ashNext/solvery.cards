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
import java.util.Set;

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
  private CardService cardService;

  private final static User user =
      new User(1, "u1", "1", "user1", "user1@a.ru",
          Collections.singleton(Role.USER), true);

  private final static User advancedUser =
      new User(4, "au4", "1", "advanced user 4", "auser4@a.ru",
          Set.of(Role.USER, Role.USER_ADVANCED), true);

  @Test
  @Override
  public void getAllByUser() {
    List<Card> exceptedCards = List.of(
        new Card(1, user, "11", 0, true),
        new Card(2, user, "12", 0, true),
        new Card(3, user, "13", 0, true)
    );

    List<Card> actualCards = cardService.getAllByUser(user);
    assertThat(actualCards).usingElementComparatorIgnoringFields("user").isEqualTo(exceptedCards);
  }

  @Test
  @Override
  public void getAllByUserAdvanced() {
    List<Card> exceptedCards = List.of(
        new Card(9, advancedUser, "41", 0, true),
        new Card(10, advancedUser, "42", 0, false),
        new Card(11, advancedUser, "43", 0, true),
        new Card(12, advancedUser, "44", 0, false)
    );

    List<Card> actualCards = cardService.getAllByUser(advancedUser);
    assertThat(actualCards).usingElementComparatorIgnoringFields("user").isEqualTo(exceptedCards);
  }

  @Test
  @Override
  public void getAllEnabledByUser() {
    List<Card> exceptedCards = List.of(
        new Card(1, user, "11", 0, true),
        new Card(2, user, "12", 0, true),
        new Card(3, user, "13", 0, true)
    );

    List<Card> actualCards = cardService.getAllEnabledByUser(user);
    assertThat(actualCards).usingElementComparatorIgnoringFields("user").isEqualTo(exceptedCards);
  }

  @Test
  @Override
  public void getAllEnabledByUserAdvanced() {
    List<Card> exceptedCards = List.of(
        new Card(9, advancedUser, "41", 0, true),
        new Card(11, advancedUser, "43", 0, true)
    );

    List<Card> actualCards = cardService.getAllEnabledByUser(advancedUser);
    assertThat(actualCards).usingElementComparatorIgnoringFields("user").isEqualTo(exceptedCards);
  }

  @Test
  @Override
  public void create() {
    Card exceptedCard = new Card(13, user, "14", 0, true);
    Card actualCard = cardService.create(new Card(user, "14"));

    assertThat(actualCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
    assertThat(cardService.getEnabledById(13)).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void createShouldReturnDuplicateNumber() {
    Card newCard = new Card(user, "11");

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> cardService.create(newCard))
        .havingRootCause()
        .withMessageContaining("cards_unique_numb_idx");
  }

  @Test
  @Override
  public void close() {
    Card exceptedCard = new Card(2, user, "12", 0, false);
    Card closedCard = cardService.close(2);
    assertThat(closedCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getEnabledById() {
    Card exceptedCard = new Card(1, user, "11", 0, true);
    Card actualCard = cardService.getEnabledById(1);
    assertThat(actualCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getEnabledByIdShouldReturnNotFound() {
    NotFoundException exception = assertThrows(NotFoundException.class, () -> cardService.getEnabledById(13));
    assertEquals(messageSourceAccessor.getMessage("card.cardByIdNotFound"), exception.getMessage());
  }

  @Test
  @Override
  public void getEnabledByCardNumb() {
    Card exceptedCard = new Card(1, user, "11", 0, true);
    Card actualCard = cardService.getEnabledByCardNumb("11");
    assertThat(actualCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getEnabledByCardNumbShouldReturnNotFound() {
    NotFoundException exception = assertThrows(NotFoundException.class, () -> cardService.getEnabledByCardNumb("14"));
    assertEquals(messageSourceAccessor.getMessage("card.numbNotFound", new Object[]{"14"}), exception.getMessage());
  }

  @Test
  @Override
  public void openBack() {
    Card exceptedCard = new Card(10, advancedUser, "42", 0, true);
    Card closedCard = cardService.openBack(10);
    assertThat(closedCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getDisabledById() {
    Card exceptedCard = new Card(4, user, "10", 0, false);
    Card actualCard = cardService.getDisabledById(4);
    assertThat(actualCard).isEqualToIgnoringGivenFields(exceptedCard, "user");
  }

  @Test
  @Override
  public void getDisabledByIdShouldReturnNotFound() {
    NotFoundException exception = assertThrows(NotFoundException.class, () -> cardService.getDisabledById(1));
    assertThat(exception.getMessage()).isEqualTo(messageSourceAccessor.getMessage("card.cardByIdNotFound"));
  }
}
