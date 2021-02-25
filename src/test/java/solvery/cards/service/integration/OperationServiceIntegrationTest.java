package solvery.cards.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.StringUtils;
import solvery.cards.model.Operation;
import solvery.cards.service.CardService;
import solvery.cards.service.OperationService;
import solvery.cards.service.OperationServiceInterfaceTest;
import solvery.cards.util.exception.BalanceOutRangeException;
import solvery.cards.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static solvery.cards.OperationTestData.*;

@Sql(
    value = {"/create-users-before.sql", "/create-cards-before.sql", "/create-operations-before.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/create-cards-after.sql", "/create-users-after.sql", "/create-operations-after.sql"},
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OperationServiceIntegrationTest extends AbstractServiceIntegrationTest implements OperationServiceInterfaceTest {

  @Autowired
  private OperationService operationService;

  @Autowired
  private CardService cardService;

  @Test
  @Override
  public void get() {
    Operation actualOperation = operationService.get(OPERATION1_CARD1.getId());

    assertThat(actualOperation).usingRecursiveComparison().ignoringFields("card").isEqualTo(OPERATION1_CARD1);
  }

  @Test
  @Override
  public void getShouldReturnMotFound() {
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> operationService.get(OPERATION_NOT_FOUND_ID));
    assertEquals(messageSourceAccessor.getMessage("operation.notFound"), exception.getMessage());
  }

  @Test
  @Override
  public void create() {
    LocalDateTime now = LocalDateTime.of(2021, Month.JANUARY, 26, 10, 0, 0);
    Operation exceptedOperation = OPERATION_DEPOSIT_CARD1;
    exceptedOperation.setDateTime(now);
    Operation actualOperation =
        operationService.create(
            new Operation(cardService.getEnabledById(CARD1.getId()), OPERATION_DEPOSIT_CARD1.getSum(), now));

    assertThat(actualOperation).usingRecursiveComparison().ignoringFields("card").isEqualTo(exceptedOperation);
    assertThat(operationService.get(OPERATION_DEPOSIT_CARD1.getId()))
        .usingRecursiveComparison().ignoringFields("card").isEqualTo(exceptedOperation);
    assertEquals(CARD1.getBalance() + OPERATION_DEPOSIT_CARD1.getSum(),
        cardService.getEnabledById(CARD1.getId()).getBalance());
  }

  @Test
  @Override
  public void addMoney() {
    Operation exceptedOperation = OPERATION_DEPOSIT_CARD1;
    Operation actualOperation = operationService.addMoney(CARD1.getId(), OPERATION_DEPOSIT_CARD1.getSum());

    assertThat(actualOperation)
        .usingRecursiveComparison().ignoringFields("card", "dateTime").isEqualTo(exceptedOperation);
    assertThat(operationService.get(OPERATION_DEPOSIT_CARD1.getId()))
        .usingRecursiveComparison().ignoringFields("card", "dateTime").isEqualTo(exceptedOperation);
    assertEquals(CARD1.getBalance() + OPERATION_DEPOSIT_CARD1.getSum(),
        cardService.getEnabledById(CARD1.getId()).getBalance());
  }

  @Test
  @Override
  public void addMoneyShouldReturnBalanceOutRangeOnOverBalance() {
    BalanceOutRangeException exception =
        assertThrows(BalanceOutRangeException.class,
            () -> operationService.addMoney(CARD1.getId(), SUM_OVER_BALANCE_CARD1));
    assertEquals(
        messageSourceAccessor.getMessage("operation.over.balance", new Object[]{CARD1.getNumb()}),
        exception.getMessage());
    assertEquals(CARD1.getBalance(), cardService.getEnabledById(CARD1.getId()).getBalance());
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION1_NOT_CREATED_ID));
  }

  @Test
  @Override
  public void withdrawMoney() {
    Operation exceptedOperation = OPERATION_WITHDRAW_CARD1;
    Operation actualOperation = operationService.withdrawMoney(CARD1.getId(), -OPERATION_WITHDRAW_CARD1.getSum());

    assertThat(actualOperation)
        .usingRecursiveComparison().ignoringFields("card", "dateTime").isEqualTo(exceptedOperation);
    assertThat(operationService.get(OPERATION_WITHDRAW_CARD1.getId()))
        .usingRecursiveComparison().ignoringFields("card", "dateTime").isEqualTo(exceptedOperation);
    assertEquals(CARD1.getBalance() + OPERATION_WITHDRAW_CARD1.getSum(),
        cardService.getEnabledById(CARD1.getId()).getBalance());
  }

  @Test
  @Override
  public void withdrawMoneyShouldReturnBalanceOutRangeOnLowerBalance() {
    BalanceOutRangeException exception =
        assertThrows(BalanceOutRangeException.class,
            () -> operationService.withdrawMoney(CARD1.getId(), SUM_LOWER_BALANCE_CARD1));
    assertEquals(
        messageSourceAccessor.getMessage("operation.lower.balance", new Object[]{CARD1.getNumb()}),
        exception.getMessage());
    assertEquals(CARD1.getBalance(), cardService.getEnabledById(CARD1.getId()).getBalance());
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION1_NOT_CREATED_ID));
  }

  @Test
  @Override
  public void transferMoney() {
    operationService.transferMoney(CARD1.getId(), CARD2.getNumb(), OPERATION_TRANSFER_TO_CARD2.getSum());
    Operation actualSenderOperation = operationService.get(OPERATION_TRANSFER_FROM_CARD1.getId());
    Operation actualRecipientOperation = operationService.get(OPERATION_TRANSFER_TO_CARD2.getId());

    assertThat(actualSenderOperation)
        .usingRecursiveComparison().ignoringFields("card", "dateTime").isEqualTo(OPERATION_TRANSFER_FROM_CARD1);
    assertThat(actualRecipientOperation)
        .usingRecursiveComparison().ignoringFields("card", "dateTime").isEqualTo(OPERATION_TRANSFER_TO_CARD2);
    assertEquals(CARD1.getBalance() + OPERATION_TRANSFER_FROM_CARD1.getSum(),
        cardService.getEnabledById(CARD1.getId()).getBalance());
    assertEquals(CARD2.getBalance() + OPERATION_TRANSFER_TO_CARD2.getSum(),
        cardService.getEnabledById(CARD2.getId()).getBalance());
  }

  @Test
  public void transferMoneyShouldReturnBalanceOutRangeOnOverBalance() {
    BalanceOutRangeException exception =
        assertThrows(BalanceOutRangeException.class,
            () -> operationService.transferMoney(CARD3.getId(), CARD2.getNumb(), SUM_OVER_BALANCE_CARD1));
    assertEquals(
        messageSourceAccessor.getMessage("operation.over.balance", new Object[]{CARD2.getNumb()}),
        exception.getMessage());
    assertEquals(CARD2.getBalance(), cardService.getEnabledById(CARD2.getId()).getBalance());
    assertEquals(CARD3.getBalance(), cardService.getEnabledById(CARD3.getId()).getBalance());
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION1_NOT_CREATED_ID));
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION2_NOT_CREATED_ID));
  }

  @Test
  public void transferMoneyShouldReturnBalanceOutRangeOnLowerBalance() {
    BalanceOutRangeException exception =
        assertThrows(BalanceOutRangeException.class,
            () -> operationService.transferMoney(CARD1.getId(), CARD2.getNumb(), SUM_LOWER_BALANCE_CARD1));
    assertEquals(
        messageSourceAccessor.getMessage("operation.lower.balance", new Object[]{CARD1.getNumb()}),
        exception.getMessage());
    assertEquals(CARD1.getBalance(), cardService.getEnabledById(CARD1.getId()).getBalance());
    assertEquals(CARD2.getBalance(), cardService.getEnabledById(CARD2.getId()).getBalance());
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION1_NOT_CREATED_ID));
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION2_NOT_CREATED_ID));
  }

  @Test
  public void transferMoneyShouldReturnNotFoundRecipientCard() {
    NotFoundException exception =
        assertThrows(NotFoundException.class,
            () -> operationService.transferMoney(CARD1.getId(), CARD_NUMB_NOT_FOUND, 1000));
    assertEquals(messageSourceAccessor.getMessage("card.numbNotFound", new Object[]{CARD_NUMB_NOT_FOUND}),
        exception.getMessage());
    assertEquals(CARD1.getBalance(), cardService.getEnabledById(CARD1.getId()).getBalance());
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION1_NOT_CREATED_ID));
    assertThrows(NotFoundException.class, () -> operationService.get(OPERATION2_NOT_CREATED_ID));
  }

  @Test
  @Override
  public void getByFilter() {
    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), null, 0, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(OPERATIONS_CARD1);
  }

  @Test
  public void getByFilterSpendingMoney() {
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> o.getSum() < 0)
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), null, -1, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterCashReceipts() {
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> o.getSum() > 0)
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), null, 1, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterCashOperations() {
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> !StringUtils.hasText(o.getRecipientCardNumb()))
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), null, 0, 1, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterTransferOperations() {
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> StringUtils.hasText(o.getRecipientCardNumb()))
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), null, 0, 2, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterWithRecipient() {
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> CARD2.getNumb().equals(o.getRecipientCardNumb()))
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), CARD2.getNumb(), 0, 0, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterOnStartDate() {
    LocalDate startDate = LocalDate.of(2021, 1, 22);
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> o.getDateTime().toLocalDate().isAfter(startDate.minusDays(1)))
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), null, 0, 0, startDate, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterOnEndDate() {
    LocalDate endDate = LocalDate.of(2021, 1, 21);
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> o.getDateTime().toLocalDate().isBefore(endDate.plusDays(1)))
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), null, 0, 0, null, endDate);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterOnSomeParams() {
    List<Operation> exceptedOperations = List.of(OPERATION3_CARD1);

    List<Operation> actualOperations =
        operationService.getByFilter(CARD1.getId(), CARD2.getNumb(), -1, 2,
            LocalDate.of(2021, 1, 20),
            LocalDate.of(2021, 1, 21));

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }

  @Test
  public void getByFilterWithCollisionOnCashAndRecipient() {
    List<Operation> exceptedOperations =
        OPERATIONS_CARD1.stream()
            .filter(o -> !StringUtils.hasText(o.getRecipientCardNumb()))
            .collect(Collectors.toList());

    List<Operation> actualOperations =
        operationService.getByFilter(
            CARD1.getId(), CARD2.getNumb(), 0, 1, null, null);

    assertThat(actualOperations).usingElementComparatorIgnoringFields("card").isEqualTo(exceptedOperations);
  }
}
