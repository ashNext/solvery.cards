package solvery.cards.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.repository.CardRepository;
import solvery.cards.util.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

  @Mock
  private CardRepository repository;

  @InjectMocks
  private CardService service;

  private final User user = new User(1, "u1", "@@", "user1", "a@b.ru");
  private final Card exceptedCard = new Card(1, user, "11", 0);

  @Test
  void getAllEnabledByUser() {
    List<Card> exceptedCards = List.of(
        new Card(1, user, "11", 0),
        new Card(2, user, "12", 1000)
    );
    when(repository.getAllByUser(user, true)).thenReturn(exceptedCards);
    List<Card> actualCards = service.getAllEnabledByUser(user);
    assertIterableEquals(exceptedCards, actualCards);

    verify(repository).getAllByUser(user, true);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void create() {
    exceptedCard.setEnabled(true);
    when(repository.save(exceptedCard)).thenReturn(exceptedCard);

    Card actualCard = service.create(new Card(1, user, "11", null));

    assertEquals(exceptedCard, actualCard);
    verify(repository).save(exceptedCard);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void create_duplicateNumber() {
    when(repository.save(exceptedCard)).thenThrow(new DataIntegrityViolationException(""));

    assertThrows(DataIntegrityViolationException.class, () -> service.create(exceptedCard));
  }

  @Test
  void update() {
  }

  @Test
  void getEnabledById() {
    when(repository.findByIdAndEnabledTrue(1)).thenReturn(Optional.of(exceptedCard));

    Card actualCard = service.getEnabledById(1);

    assertEquals(exceptedCard, actualCard);
    verify(repository, times(1)).findByIdAndEnabledTrue(1);
  }

  @Test
  void getEnabledById_notFound() {
    when(repository.findByIdAndEnabledTrue(1)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getEnabledById(1));
  }

  @Test
  void getEnabledByCardNumb() {
    when(repository.findByNumbAndEnabledTrue("11")).thenReturn(Optional.of(exceptedCard));

    Card actualCard = service.getEnabledByCardNumb("11");

    assertEquals(exceptedCard, actualCard);
    verify(repository, times(1)).findByNumbAndEnabledTrue("11");
  }

  @Test
  void getEnabledByCardNumb_notFound() {
    when(repository.findByNumbAndEnabledTrue("11")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getEnabledByCardNumb("11"));
  }
}