package solvery.cards.service.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import solvery.cards.model.Card;
import solvery.cards.repository.CardRepository;
import solvery.cards.service.CardService;
import solvery.cards.service.CardServiceInterfaceTest;
import solvery.cards.util.exception.NotFoundException;

class CardServiceUnitTest extends AbstractServiceUnitTest implements CardServiceInterfaceTest {

  @Mock
  private CardRepository repository;

  @InjectMocks
  private CardService service;

  @Test
  @Override
  public void getAllByUser() {
    List<Card> exceptedCards = List.of(
        new Card(1, exceptedUser, "11", 0, true),
        new Card(2, exceptedUser, "12", 0, true),
        new Card(3, exceptedUser, "13", 0, true)
    );
    when(repository.getAllByUserAndEnabled(exceptedUser, true)).thenReturn(exceptedCards);
    List<Card> actualCards = service.getAllByUser(exceptedUser);
    assertIterableEquals(exceptedCards, actualCards);

    verify(repository).getAllByUserAndEnabled(exceptedUser, true);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void getAllByUserAdvanced() {
    List<Card> exceptedCards = List.of(
        new Card(9, exceptedAdvancedUser, "41", 0, true),
        new Card(10, exceptedAdvancedUser, "42", 0, false),
        new Card(11, exceptedAdvancedUser, "43", 0, true),
        new Card(12, exceptedAdvancedUser, "44", 0, false)
    );
    when(repository.getAllByUser(exceptedAdvancedUser)).thenReturn(exceptedCards);
    List<Card> actualCards = service.getAllByUser(exceptedAdvancedUser);
    assertIterableEquals(exceptedCards, actualCards);

    verify(repository).getAllByUser(exceptedAdvancedUser);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void getAllEnabledByUser() {
    List<Card> exceptedCards = List.of(
        new Card(1, exceptedUser, "11", 0, true),
        new Card(2, exceptedUser, "12", 0, true),
        new Card(3, exceptedUser, "13", 0, true)
    );
    when(repository.getAllByUserAndEnabled(exceptedUser, true)).thenReturn(exceptedCards);
    List<Card> actualCards = service.getAllEnabledByUser(exceptedUser);
    assertIterableEquals(exceptedCards, actualCards);

    verify(repository).getAllByUserAndEnabled(exceptedUser, true);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void getAllEnabledByUserAdvanced() {
    List<Card> exceptedCards = List.of(
        new Card(9, exceptedAdvancedUser, "41", 0, true),
        new Card(10, exceptedAdvancedUser, "42", 0, false),
        new Card(11, exceptedAdvancedUser, "43", 0, true),
        new Card(12, exceptedAdvancedUser, "44", 0, false)
    );
    when(repository.getAllByUser(exceptedAdvancedUser)).thenReturn(exceptedCards);
    List<Card> actualCards = service.getAllEnabledByUser(exceptedAdvancedUser);
    assertIterableEquals(
        exceptedCards.stream()
            .filter(Card::isEnabled)
            .collect(Collectors.toList()),
        actualCards);

    verify(repository).getAllByUser(exceptedAdvancedUser);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void create() {
    when(repository.save(exceptedCard)).thenReturn(exceptedCard);

    Card actualCard = service.create(new Card(1, exceptedUser, "11", null));

    assertEquals(exceptedCard, actualCard);
    verify(repository).save(exceptedCard);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void createShouldReturnDuplicateNumber() {
    when(repository.save(exceptedCard)).thenThrow(new DataIntegrityViolationException("42"));

    DataIntegrityViolationException exception =
        assertThrows(DataIntegrityViolationException.class, () -> service.create(exceptedCard));
    assertEquals("42", exception.getMessage());
  }

  @Test
  @Override
  public void close() {
    when(repository.findByIdAndEnabledTrue(1)).thenReturn(Optional.of(exceptedCard));
    Card disabledCard = exceptedCard;
    disabledCard.setEnabled(false);
    when(repository.save(disabledCard)).thenReturn(disabledCard);

    Card actualCard = service.close(1);

    assertEquals(disabledCard, actualCard);
    verify(repository, times(1)).findByIdAndEnabledTrue(1);
    verify(repository, times(1)).save(disabledCard);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void getById() {
    when(repository.findById(10)).thenReturn(Optional.of(exceptedDisabledCard));

    Card actualCard = service.getById(10);

    assertEquals(exceptedDisabledCard, actualCard);
    verify(repository, times(1)).findById(10);
  }

  @Test
  @Override
  public void getByIdShouldReturnNotFound() {
    when(repository.findById(13)).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.getById(13));

    assertEquals("card.cardByIdNotFound", exception.getMsgCode());
  }

  @Test
  @Override
  public void getEnabledById() {
    when(repository.findByIdAndEnabledTrue(1)).thenReturn(Optional.of(exceptedCard));

    Card actualCard = service.getEnabledById(1);

    assertEquals(exceptedCard, actualCard);
    verify(repository, times(1)).findByIdAndEnabledTrue(1);
  }

  @Test
  @Override
  public void getEnabledByIdShouldReturnNotFound() {
    when(repository.findByIdAndEnabledTrue(1)).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.getEnabledById(1));

    assertEquals("card.cardByIdNotFound", exception.getMsgCode());
  }

  @Test
  @Override
  public void getEnabledByCardNumb() {
    when(repository.findByNumbAndEnabledTrue("11")).thenReturn(Optional.of(exceptedCard));

    Card actualCard = service.getEnabledByCardNumb("11");

    assertEquals(exceptedCard, actualCard);
    verify(repository, times(1)).findByNumbAndEnabledTrue("11");
  }

  @Test
  @Override
  public void getEnabledByCardNumbShouldReturnNotFound() {
    when(repository.findByNumbAndEnabledTrue("11")).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.getEnabledByCardNumb("11"));

    assertEquals("card.numbNotFound", exception.getMsgCode());
    assertEquals("11", exception.getArgs()[0]);
  }

  @Test
  @Override
  public void openBack() {
    when(repository.findByIdAndEnabledFalse(10)).thenReturn(Optional.of(exceptedDisabledCard));
    Card enabledCard = exceptedDisabledCard;
    enabledCard.setEnabled(true);
    when(repository.save(enabledCard)).thenReturn(enabledCard);

    Card actualCard = service.openBack(10);

    assertEquals(enabledCard, actualCard);
    verify(repository, times(1)).findByIdAndEnabledFalse(10);
    verify(repository, times(1)).save(enabledCard);
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void getDisabledById() {
    when(repository.findByIdAndEnabledFalse(4)).thenReturn(Optional.of(exceptedDisabledCard));

    Card actualCard = service.getDisabledById(4);

    assertEquals(exceptedDisabledCard, actualCard);
    verify(repository, times(1)).findByIdAndEnabledFalse(4);
  }

  @Test
  @Override
  public void getDisabledByIdShouldReturnNotFound() {
    when(repository.findByIdAndEnabledFalse(1)).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.getDisabledById(1));

    assertEquals("card.cardByIdNotFound", exception.getMsgCode());
  }
}