package solvery.cards.service;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;
import solvery.cards.model.Card;
import solvery.cards.model.Role;
import solvery.cards.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@Tag("slow")
public class CardCachingTest {

  @Mock
  private CardService mock;

  @Autowired
  private CardService cardService;

  @Autowired
  private OperationService operationService;

  @Autowired
  private CacheManager cacheManager;

  private final static User user =
      new User(1, "u1", "1", "user1", "user1@a.ru",
          Collections.singleton(Role.USER), true);
  private final static User advancedUser =
      new User(4, "au4", "1", "advanced user 4", "auser4@a.ru",
          Set.of(Role.USER, Role.USER_ADVANCED), true);

  private final static List<Card> exceptedCards = List.of(
      new Card(1, user, "11", 0, true),
      new Card(2, user, "12", 0, true),
      new Card(3, user, "13", 0, true)
  );

  private final static List<Card> exceptedCardsAdvanced = List.of(
      new Card(9, advancedUser, "41", 0, true),
      new Card(10, advancedUser, "42", 0, false),
      new Card(11, advancedUser, "43", 0, true),
      new Card(12, advancedUser, "44", 0, false)
  );

  private final static List<Card> exceptedEnabledCardsAdvanced = List.of(
      new Card(9, advancedUser, "41", 0, true),
      new Card(11, advancedUser, "43", 0, true)
  );

  @Configuration
  @EnableCaching
  public static class CachingTestConfig {

    @Bean
    public CardService cardServiceMockImplementation() {
      return mock(CardService.class);
    }

    @Bean
    public OperationService operationServiceMockImplementation() {
      return mock(OperationService.class);
    }

    @Bean
    public CacheManager cacheManager() {
      return new ConcurrentMapCacheManager("cards", "cardsEnabled");
    }
  }

  @BeforeEach
  void setUp() {
    Objects.requireNonNull(cacheManager.getCache("cards")).clear();
    Objects.requireNonNull(cacheManager.getCache("cardsEnabled")).clear();
    mock = AopTestUtils.getTargetObject(cardService);
    reset(mock);
    when(mock.getAllByUser(user)).thenReturn(exceptedCards);
    when(mock.getAllByUser(advancedUser)).thenReturn(exceptedCardsAdvanced);
    when(mock.getAllEnabledByUser(user)).thenReturn(exceptedCards);
    when(mock.getAllEnabledByUser(advancedUser)).thenReturn(exceptedEnabledCardsAdvanced);
  }

  @Test
  void getAllByUser() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    verify(mock, times(1)).getAllByUser(user);
  }

  @Test
  void getAllEnabledByUser() {
    assertIterableEquals(exceptedCards, cardService.getAllEnabledByUser(user));
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    assertIterableEquals(exceptedCards, cardService.getAllEnabledByUser(user));
    verify(mock, times(1)).getAllEnabledByUser(user);
  }

  @Test
  void getAllEnabledByUserAdvanced() {
    assertIterableEquals(exceptedEnabledCardsAdvanced, cardService.getAllEnabledByUser(advancedUser));
    assertIterableEquals(exceptedEnabledCardsAdvanced, cardService.getAllEnabledByUser(advancedUser));
    assertIterableEquals(exceptedEnabledCardsAdvanced, cardService.getAllEnabledByUser(advancedUser));
    verify(mock, times(1)).getAllEnabledByUser(advancedUser);
  }

  @Test
  void getAllByAnyUser() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    assertIterableEquals(exceptedCardsAdvanced, cardService.getAllByUser(advancedUser));
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    verify(mock, times(1)).getAllByUser(user);
    verify(mock, times(1)).getAllByUser(advancedUser);
  }

  @Test
  void create() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    cardService.getAllByUser(user);
    cardService.create(new Card(1, user, "13", 0));
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    verify(mock, times(2)).getAllByUser(user);
  }

  @Test
  void update() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    cardService.getAllByUser(user);
    cardService.update(new Card(2, user, "12", 100));
    cardService.getAllByUser(user);
    verify(mock, times(2)).getAllByUser(user);
  }

  @Test
  void close() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    cardService.getAllByUser(user);
    cardService.close(1);
    cardService.getAllByUser(user);
    verify(mock, times(2)).getAllByUser(user);
  }

  @Test
  void openBack() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    cardService.getAllByUser(user);
    cardService.openBack(1);
    cardService.getAllByUser(user);
    verify(mock, times(2)).getAllByUser(user);
  }

  @Test
  void addMoney() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    cardService.getAllByUser(user);
    operationService.addMoney(1, 100);
    cardService.getAllByUser(user);
    verify(mock, times(2)).getAllByUser(user);
  }

  @Test
  void withdrawMoney() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    cardService.getAllByUser(user);
    operationService.withdrawMoney(1, 100);
    cardService.getAllByUser(user);
    verify(mock, times(2)).getAllByUser(user);
  }

  @Test
  void transferMoney() {
    assertIterableEquals(exceptedCards, cardService.getAllByUser(user));
    cardService.getAllByUser(user);
    operationService.transferMoney(1, "12", 100);
    cardService.getAllByUser(user);
    verify(mock, times(2)).getAllByUser(user);
  }
}
