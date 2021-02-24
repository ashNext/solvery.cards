package solvery.cards.service.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import solvery.cards.model.Card;
import solvery.cards.model.Operation;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.CardService;
import solvery.cards.service.OperationService;
import solvery.cards.service.OperationServiceInterfaceTest;
import solvery.cards.util.exception.BalanceOutRangeException;
import solvery.cards.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Sql(
    value = {"/create-users-before.sql", "/create-cards-before.sql", "/create-operations-before.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/create-cards-after.sql", "/create-users-after.sql", "/create-operations-after.sql"},
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OperationServiceIntegrationTest extends AbstractServiceIntegrationTest implements OperationServiceInterfaceTest {

  private final static User user =
      new User(1, "u1", "1", "user1", "user1@a.ru",
          Collections.singleton(Role.USER), true);

  private static Card card1;
  private static Card card2;
  private static Card card3;

  @Autowired
  private OperationService service;

  @Autowired
  private CardService cardService;

  @BeforeEach
  void setUp() {
    card1 = new Card(1, user, "11", 6500);
    card2 = new Card(2, user, "12", 2000);
    card3 = new Card(3, user, "13", 999999000);
  }

  @Test
  @Override
  public void get() {
    Operation exceptedOperation =
        new Operation(1L, card1, null, 10000, 10000,
            LocalDateTime.of(2021, Month.JANUARY, 20, 10, 0, 0)); //2021-01-20 10:00:00
    Operation actualOperation = service.get(1L);
    assertThat(actualOperation).isEqualToIgnoringGivenFields(exceptedOperation, "card");
  }

  @Test
  @Override
  public void getShouldReturnMotFound() {
    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.get(14L));
    assertEquals(messageSourceAccessor.getMessage("operation.notFound"), exception.getMessage());
  }

  @Test
  @Override
  public void create() {
    LocalDateTime now = LocalDateTime.of(2021, Month.JANUARY, 26, 10, 0, 0);
    Operation exceptedOperation = new Operation(14L, card1, null, 1000, 7500, now);
    Operation actualOperation = service.create(new Operation(card1, 1000, now));

    assertThat(actualOperation).isEqualToIgnoringGivenFields(exceptedOperation, "card");
    assertThat(service.get(14L)).isEqualToIgnoringGivenFields(exceptedOperation, "card");
    assertEquals(card1.getBalance(), cardService.getEnabledById(1).getBalance());
  }

  @Test
  @Override
  public void addMoney() {
    Operation exceptedOperation = new Operation(14L, card1, null, 1500, 8000, LocalDateTime.now());
    Operation actualOperation = service.addMoney(1, 1500);

    assertThat(actualOperation).isEqualToIgnoringGivenFields(exceptedOperation, "card", "dateTime");
    assertThat(service.get(14L)).isEqualToIgnoringGivenFields(exceptedOperation, "card", "dateTime");
    assertEquals(card1.getBalance() + 1500, cardService.getEnabledById(1).getBalance());
  }

  @Test
  @Override
  public void addMoneyShouldReturnBalanceOutRangeOnOverBalance() {
    BalanceOutRangeException exception = assertThrows(BalanceOutRangeException.class, () -> service.addMoney(1, 999999000));
    assertEquals(
        messageSourceAccessor.getMessage("operation.over.balance", new Object[]{"11"}),
        exception.getMessage());
    assertEquals(card1.getBalance(), cardService.getEnabledById(1).getBalance());
    assertThrows(NotFoundException.class, () -> service.get(14L));
  }

  @Test
  @Override
  public void withdrawMoney() {
    Operation exceptedOperation = new Operation(14L, card1, null, -1500, 5000, LocalDateTime.now());
    Operation actualOperation = service.withdrawMoney(1, 1500);

    assertThat(actualOperation).isEqualToIgnoringGivenFields(exceptedOperation, "card", "dateTime");
    assertThat(service.get(14L)).isEqualToIgnoringGivenFields(exceptedOperation, "card", "dateTime");
    assertEquals(card1.getBalance() - 1500, cardService.getEnabledById(1).getBalance());
  }

  @Test
  @Override
  public void withdrawMoneyShouldReturnBalanceOutRangeOnLowerBalance() {
    BalanceOutRangeException exception = assertThrows(BalanceOutRangeException.class, () -> service.withdrawMoney(1, 7000));
    assertEquals(
        messageSourceAccessor.getMessage("operation.lower.balance", new Object[]{"11"}),
        exception.getMessage());
    assertEquals(card1.getBalance(), cardService.getEnabledById(1).getBalance());
    assertThrows(NotFoundException.class, () -> service.get(14L));
  }

  @Test
  @Override
  public void transferMoney() {
    Operation exceptedSenderOperation = new Operation(14L, card1, "12", -2000, 4500, LocalDateTime.now());
    Operation exceptedRecipientOperation = new Operation(15L, card2, "11", 2000, 4000, LocalDateTime.now());

    service.transferMoney(1, "12", 2000);
    Operation actualSenderOperation = service.get(14L);
    Operation actualRecipientOperation = service.get(15L);

    assertThat(actualSenderOperation).isEqualToIgnoringGivenFields(exceptedSenderOperation, "card", "dateTime");
    assertThat(actualRecipientOperation).isEqualToIgnoringGivenFields(exceptedRecipientOperation, "card", "dateTime");
    assertEquals(card1.getBalance() - 2000, cardService.getEnabledById(1).getBalance());
    assertEquals(card2.getBalance() + 2000, cardService.getEnabledById(2).getBalance());
  }

  @Test
  public void transferMoneyShouldReturnBalanceOutRangeOnOverBalance() {
    BalanceOutRangeException exception =
        assertThrows(BalanceOutRangeException.class, () -> service.transferMoney(3, "12", 999999000));
    assertEquals(
        messageSourceAccessor.getMessage("operation.over.balance", new Object[]{"12"}),
        exception.getMessage());
    assertEquals(card2.getBalance(), cardService.getEnabledById(2).getBalance());
    assertEquals(card3.getBalance(), cardService.getEnabledById(3).getBalance());
    assertThrows(NotFoundException.class, () -> service.get(14L));
    assertThrows(NotFoundException.class, () -> service.get(15L));
  }

  @Test
  public void transferMoneyShouldReturnBalanceOutRangeOnLowerBalance() {
    BalanceOutRangeException exception =
        assertThrows(BalanceOutRangeException.class, () -> service.transferMoney(1, "12", 10000));
    assertEquals(
        messageSourceAccessor.getMessage("operation.lower.balance", new Object[]{"11"}),
        exception.getMessage());
    assertEquals(card1.getBalance(), cardService.getEnabledById(1).getBalance());
    assertEquals(card2.getBalance(), cardService.getEnabledById(2).getBalance());
    assertThrows(NotFoundException.class, () -> service.get(14L));
    assertThrows(NotFoundException.class, () -> service.get(15L));
  }

  @Test
  public void transferMoneyShouldReturnNotFoundRecipientCard() {
    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.transferMoney(1, "111", 1000));
    assertEquals(messageSourceAccessor.getMessage("card.numbNotFound", new Object[]{"111"}), exception.getMessage());
    assertEquals(card1.getBalance(), cardService.getEnabledById(1).getBalance());
    assertThrows(NotFoundException.class, () -> service.get(14L));
    assertThrows(NotFoundException.class, () -> service.get(15L));
  }

  @Test
  @Override
  public void getByFilter() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(1L, card1, null, 10000, 10000,
            LocalDateTime.of(2021, 1, 20, 10, 0, 0)),
        new Operation(2L, card1, null, -5000, 5000,
            LocalDateTime.of(2021, 1, 21, 20, 0, 0)),
        new Operation(3L, card1, "12", -2500, 2500,
            LocalDateTime.of(2021, 1, 21, 21, 0, 0)),
        new Operation(5L, card1, null, 10000, 12500,
            LocalDateTime.of(2021, 1, 22, 10, 0, 0)),
        new Operation(6L, card1, null, -5000, 7500,
            LocalDateTime.of(2021, 1, 22, 12, 0, 0)),
        new Operation(7L, card1, "13", -2000, 5500,
            LocalDateTime.of(2021, 1, 23, 15, 0, 0)),
        new Operation(12L, card1, "12", 1000, 6500,
            LocalDateTime.of(2021, 1, 25, 13, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, null, 0, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterSpendingMoney() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(2L, card1, null, -5000, 5000,
            LocalDateTime.of(2021, 1, 21, 20, 0, 0)),
        new Operation(3L, card1, "12", -2500, 2500,
            LocalDateTime.of(2021, 1, 21, 21, 0, 0)),
        new Operation(6L, card1, null, -5000, 7500,
            LocalDateTime.of(2021, 1, 22, 12, 0, 0)),
        new Operation(7L, card1, "13", -2000, 5500,
            LocalDateTime.of(2021, 1, 23, 15, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, null, -1, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterCashReceipts() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(1L, card1, null, 10000, 10000,
            LocalDateTime.of(2021, 1, 20, 10, 0, 0)),
        new Operation(5L, card1, null, 10000, 12500,
            LocalDateTime.of(2021, 1, 22, 10, 0, 0)),
        new Operation(12L, card1, "12", 1000, 6500,
            LocalDateTime.of(2021, 1, 25, 13, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, null, 1, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterCashOperations() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(1L, card1, null, 10000, 10000,
            LocalDateTime.of(2021, 1, 20, 10, 0, 0)),
        new Operation(2L, card1, null, -5000, 5000,
            LocalDateTime.of(2021, 1, 21, 20, 0, 0)),
        new Operation(5L, card1, null, 10000, 12500,
            LocalDateTime.of(2021, 1, 22, 10, 0, 0)),
        new Operation(6L, card1, null, -5000, 7500,
            LocalDateTime.of(2021, 1, 22, 12, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, null, 0, 1, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterTransferOperations() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(3L, card1, "12", -2500, 2500,
            LocalDateTime.of(2021, 1, 21, 21, 0, 0)),
        new Operation(7L, card1, "13", -2000, 5500,
            LocalDateTime.of(2021, 1, 23, 15, 0, 0)),
        new Operation(12L, card1, "12", 1000, 6500,
            LocalDateTime.of(2021, 1, 25, 13, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, null, 0, 2, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterWithRecipient() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(3L, card1, "12", -2500, 2500,
            LocalDateTime.of(2021, 1, 21, 21, 0, 0)),
        new Operation(12L, card1, "12", 1000, 6500,
            LocalDateTime.of(2021, 1, 25, 13, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, "12", 0, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterOnStartDate() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(5L, card1, null, 10000, 12500,
            LocalDateTime.of(2021, 1, 22, 10, 0, 0)),
        new Operation(6L, card1, null, -5000, 7500,
            LocalDateTime.of(2021, 1, 22, 12, 0, 0)),
        new Operation(7L, card1, "13", -2000, 5500,
            LocalDateTime.of(2021, 1, 23, 15, 0, 0)),
        new Operation(12L, card1, "12", 1000, 6500,
            LocalDateTime.of(2021, 1, 25, 13, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, null, 0, 0, LocalDate.of(2021, 1, 22), null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterOnEndDate() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(1L, card1, null, 10000, 10000,
            LocalDateTime.of(2021, 1, 20, 10, 0, 0)),
        new Operation(2L, card1, null, -5000, 5000,
            LocalDateTime.of(2021, 1, 21, 20, 0, 0)),
        new Operation(3L, card1, "12", -2500, 2500,
            LocalDateTime.of(2021, 1, 21, 21, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, null, 0, 0, null, LocalDate.of(2021, 1, 21));

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterOnSomeParams() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(3L, card1, "12", -2500, 2500,
            LocalDateTime.of(2021, 1, 21, 21, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, "12", -1, 2, LocalDate.of(2021, 1, 20), LocalDate.of(2021, 1, 21));

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterWithCollisionOnCashAndRecipient() {
    List<Operation> exceptedOperations = new ArrayList<>(List.of(
        new Operation(1L, card1, null, 10000, 10000,
            LocalDateTime.of(2021, 1, 20, 10, 0, 0)),
        new Operation(2L, card1, null, -5000, 5000,
            LocalDateTime.of(2021, 1, 21, 20, 0, 0)),
        new Operation(5L, card1, null, 10000, 12500,
            LocalDateTime.of(2021, 1, 22, 10, 0, 0)),
        new Operation(6L, card1, null, -5000, 7500,
            LocalDateTime.of(2021, 1, 22, 12, 0, 0))
    ));
    exceptedOperations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    List<Operation> actualOperations = service.getByFilter(1, "12", 0, 1, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }
}
