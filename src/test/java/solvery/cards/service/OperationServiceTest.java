package solvery.cards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import solvery.cards.model.Card;
import solvery.cards.model.Operation;
import solvery.cards.model.User;
import solvery.cards.repository.OperationRepository;
import solvery.cards.util.exception.BalanceOutRangeException;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class OperationServiceTest {

  @Mock
  private OperationRepository repository;

  @Mock
  private CardService cardService;

  @Mock
  private MessageSourceAccessor messageSourceAccessor;

  @InjectMocks
  private OperationService service;

  private final static User user = new User(1, "u1", "@@", "user1", "a@b.ru");

  private final static Card exceptedCard1 = new Card(1, user, "11", 3000, true);
  private final static Card exceptedCard2 = new Card(2, user, "12", 6000, true);

  private final static LocalDateTime exceptedNow = LocalDateTime.of(2021, 1, 2, 11, 0);
  private final static Operation exceptedOperation =
      new Operation(1L, exceptedCard1, "", 1000, 3000, exceptedNow);


  @Test
  void create() {
    Card actualCard = new Card(1, user, "11", 2000, true);
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
  void addMoney() {
    Card actualCard = new Card(1, user, "11", 2000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(repository.save(new Operation(actualCard, 1000, any()))).thenReturn(exceptedOperation);

    Operation actualOperation = service.addMoney(actualCard.getId(), 1000);

    assertEquals(exceptedOperation, actualOperation);
    assertEquals(exceptedCard1, actualCard);
    verify(repository, times(1)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  void addMoney_overBalance() {
    Card actualCard = new Card(1, user, "11", 2000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(messageSourceAccessor.getMessage(
        eq("operation.over.balance"),
        eq(new Object[]{"11"}))
    ).thenReturn("1");

    assertThrows(
        BalanceOutRangeException.class,
        () -> service.addMoney(actualCard.getId(), 999999900), "42");
    verify(repository, times(0)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  void withdrawMoney() {
    Card actualCard = new Card(1, user, "11", 5000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(repository.save(new Operation(actualCard, 2000, any()))).thenReturn(exceptedOperation);

    Operation actualOperation = service.withdrawMoney(actualCard.getId(), 2000);

    assertEquals(exceptedOperation, actualOperation);
    assertEquals(exceptedCard1, actualCard);
    verify(repository, times(1)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  void withdrawMoney_lowerBalance() {
    Card actualCard = new Card(1, user, "11", 5000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(actualCard);
    when(messageSourceAccessor.getMessage(
        eq("operation.lower.balance"),
        eq(new Object[]{"11"}))
    ).thenReturn("1");

    assertThrows(
        BalanceOutRangeException.class,
        () -> service.withdrawMoney(actualCard.getId(), 999999900), "42");

    verify(repository, times(0)).save(any());
    verifyNoMoreInteractions(repository);
  }

  @Test
  void transferMoney() {
    Card card = new Card(1, user, "11", 4000, true);
    Card recipientCard = new Card(2, user, "12", 5000, true);

    when(cardService.getEnabledById(eq(1))).thenReturn(card);
    when(cardService.getEnabledByCardNumb(eq("12"))).thenReturn(recipientCard);

    service.transferMoney(1, "12", 1000);

    assertEquals(exceptedCard1, card);
    assertEquals(exceptedCard2, recipientCard);
    verify(repository, times(2)).save(any());
  }

  @Test
  void getByFilter() {
    Card card = new Card(1, user, "11", 7000, true);
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
//        eq(
//        Specification.where(withCard(card))
//            .and(withRecipient(""))
//            .and(withDirection(0))
//            .and(withType(0))
//            .and(fromDate(null))
//            .and(toDate(null))),
        eq(Sort.by(Sort.Direction.DESC, "dateTime")))
    ).thenReturn(operations);

    List<Operation> actualOperations =
        service.getByFilter(card.getId(), null, 0, 0, null, null);

    assertEquals(operations, actualOperations);
  }
}