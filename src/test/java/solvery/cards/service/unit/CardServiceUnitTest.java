package solvery.cards.service.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import solvery.cards.model.Card;
import solvery.cards.repository.CardRepository;
import solvery.cards.service.CardService;
import solvery.cards.service.CardServiceInterfaceTest;
import solvery.cards.util.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceUnitTest extends AbstractServiceUnitTest implements CardServiceInterfaceTest {

  @Mock
  private CardRepository repository;

  @InjectMocks
  private CardService service;

  @Test
  @Override
  public void getAllEnabledByUser() {
    List<Card> exceptedCards = List.of(
        new Card(1, exceptedUser, "11", 0),
        new Card(2, exceptedUser, "12", 1000)
    );
    when(repository.getAllByUser(exceptedUser, true)).thenReturn(exceptedCards);
    List<Card> actualCards = service.getAllEnabledByUser(exceptedUser);
    assertIterableEquals(exceptedCards, actualCards);

    verify(repository).getAllByUser(exceptedUser, true);
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
    when(messageSourceAccessor.getMessage(
        eq("card.cardByIdNotFound"),
        eq(LocaleContextHolder.getLocale()))
    ).thenReturn("42");

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.getEnabledById(1));
    assertEquals("42", exception.getMessage());
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
    when(messageSourceAccessor.getMessage(
        eq("card.numbNotFound"),
        eq(new Object[]{"11"}),
        eq(LocaleContextHolder.getLocale()))
    ).thenReturn("42");

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.getEnabledByCardNumb("11"));
    assertEquals("42", exception.getMessage());
  }
}