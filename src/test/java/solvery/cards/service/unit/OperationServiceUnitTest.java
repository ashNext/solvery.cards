package solvery.cards.service.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import solvery.cards.model.Card;
import solvery.cards.model.Operation;
import solvery.cards.repository.OperationRepository;
import solvery.cards.service.CardService;
import solvery.cards.service.OperationService;
import solvery.cards.service.OperationServiceInterfaceTest;
import solvery.cards.util.exception.BalanceOutRangeException;
import solvery.cards.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OperationServiceUnitTest extends AbstractServiceUnitTest implements OperationServiceInterfaceTest {

  @Mock
  private OperationRepository repository;

  @Mock
  private CardService cardService;

  @InjectMocks
  private OperationService service;

  private final static Card exceptedCard1 = new Card(1, exceptedUser, "11", 3000, true);
  private final static Card exceptedCard2 = new Card(2, exceptedUser, "12", 6000, true);

  private final static LocalDateTime exceptedNow = LocalDateTime.of(2021, 1, 2, 11, 0);
  private final static Operation exceptedOperation =
      new Operation(1L, exceptedCard1, "", 1000, 3000, exceptedNow);

  @Test
  @Override
  public void get() {
    when(repository.findById(1L)).thenReturn(Optional.of(exceptedOperation));

    Operation actualOperation = service.get(1L);

    assertEquals(exceptedOperation, actualOperation);
    verify(repository, times(1)).findById(1L);
  }

  @Test
  @Override
  public void getShouldReturnMotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());
    when(messageSourceAccessor.getMessage("operation.notFound")).thenReturn("42");

    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.get(1L));
    assertEquals("42", exception.getMessage());
  }

  @Test
  @Override
  public void create() {
    Card actualCard = new Card(1, exceptedUser, "11", 2000, true);
    when(
        repository.save(new Operation(1L, actualCard, "", 1000, any()))
    )
        .thenReturn(exceptedOperation);

    Operation actualOperation =
        service.create(
            new Operation(1L, actualCard, "", 1000,
                LocalDateTime.of(2021, 1, 2, 11, 0)));

    assertEquals(exceptedOperation, actualOperation);
    assertEquals(exceptedCard1, actualCard);
    verify(repository, times(1)).save(exceptedOperation);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void addMoney() {
    Card actualCard = new Card(1, exceptedUser, "11", 2000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(repository.save(new Operation(actualCard, 1000, any()))).thenReturn(exceptedOperation);

    Operation actualOperation = service.addMoney(actualCard.getId(), 1000);

    assertEquals(exceptedOperation, actualOperation);
    assertEquals(exceptedCard1, actualCard);
    verify(repository, times(1)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void addMoneyShouldReturnBalanceOutRangeOnOverBalance() {
    Card actualCard = new Card(1, exceptedUser, "11", 2000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(messageSourceAccessor.getMessage(
        eq("operation.over.balance"),
        eq(new Object[]{"11"}))
    ).thenReturn("42");

    BalanceOutRangeException exception = assertThrows(
        BalanceOutRangeException.class,
        () -> service.addMoney(actualCard.getId(), 999999900), "42");
    assertEquals("42", exception.getMessage());
    verify(repository, times(0)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void withdrawMoney() {
    Card actualCard = new Card(1, exceptedUser, "11", 5000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(repository.save(new Operation(actualCard, 2000, any()))).thenReturn(exceptedOperation);

    Operation actualOperation = service.withdrawMoney(actualCard.getId(), 2000);

    assertEquals(exceptedOperation, actualOperation);
    assertEquals(exceptedCard1, actualCard);
    verify(repository, times(1)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void withdrawMoneyShouldReturnBalanceOutRangeOnLowerBalance() {
    Card actualCard = new Card(1, exceptedUser, "11", 5000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(messageSourceAccessor.getMessage(
        eq("operation.lower.balance"),
        eq(new Object[]{"11"}))
    ).thenReturn("42");

    BalanceOutRangeException exception = assertThrows(
        BalanceOutRangeException.class,
        () -> service.withdrawMoney(actualCard.getId(), 999999900));

    assertEquals("42", exception.getMessage());
    verify(repository, times(0)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void transferMoney() {
    Card card = new Card(1, exceptedUser, "11", 4000, true);
    Card recipientCard = new Card(2, exceptedUser, "12", 5000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(card);
    when(cardService.getEnabledByCardNumb(eq("12"))).thenReturn(recipientCard);

    service.transferMoney(1, "12", 1000);

    assertEquals(exceptedCard1, card);
    assertEquals(exceptedCard2, recipientCard);
    verify(repository, times(2)).save(any());
  }

  @Test
  @Override
  public void getByFilter() {
    Card card = new Card(1, exceptedUser, "11", 7000, true);
    List<Operation> operations = new ArrayList<>(List.of(
        new Operation(1L, card, null, 10000, 10000,
            LocalDateTime.of(2021, 1, 2, 11, 0)),
        new Operation(2L, card, null, -2000, 8000,
            LocalDateTime.of(2021, 1, 2, 12, 0)),
        new Operation(3L, card, "12", -3000, 5000,
            LocalDateTime.of(2021, 1, 3, 10, 0)),
        new Operation(4L, card, "11", 3000, 3000,
            LocalDateTime.of(2021, 1, 3, 10, 0)),
        new Operation(5L, card, "11", -1000, 2000,
            LocalDateTime.of(2021, 1, 4, 13, 0)),
        new Operation(6L, card, "12", 1000, 6000,
            LocalDateTime.of(2021, 1, 4, 13, 0)),
        new Operation(7L, card, null, -2000, 4000,
            LocalDateTime.of(2021, 1, 5, 11, 0)),
        new Operation(8L, card, null, 3000, 7000,
            LocalDateTime.of(2021, 1, 5, 12, 0))
    ));
    operations.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));

    when(cardService.getEnabledById(eq(1))).thenReturn(card);
    when(repository.findAll(
        any(Specification.class),
        eq(Sort.by(Sort.Direction.DESC, "dateTime")))
    ).thenReturn(operations);

    List<Operation> actualOperations =
        service.getByFilter(card.getId(), null, 0, 0, null, null);

    assertEquals(operations, actualOperations);
  }
}