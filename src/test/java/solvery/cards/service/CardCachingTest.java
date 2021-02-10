package solvery.cards.service;

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
import solvery.cards.model.User;

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
  private CardService service;

  @Autowired
  private OperationService operationService;

  @Autowired
  private CacheManager cacheManager;

  private final static User user = new User(1, "u1", "@@", "user1", "a@b.ru");

  private final static List<Card> exceptedCards = List.of(
      new Card(1, user, "11", 0),
      new Card(2, user, "12", 1000)
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
      return new ConcurrentMapCacheManager("cards");
    }
  }

  @BeforeEach
  void setUp() {
    Objects.requireNonNull(cacheManager.getCache("cards")).clear();
    mock = AopTestUtils.getTargetObject(service);
    reset(mock);
    when(mock.getAllEnabledByUser(user)).thenReturn(exceptedCards);
  }

  @Test
  void getAllEnabledByUser() {
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    verify(mock, times(1)).getAllEnabledByUser(user);
  }

  @Test
  void create() {
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    service.getAllEnabledByUser(user);
    service.create(new Card(1, user, "13", 0));
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    verify(mock, times(2)).getAllEnabledByUser(user);
  }

  @Test
  void update() {
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    service.getAllEnabledByUser(user);
    service.update(new Card(2, user, "12", 100));
    service.getAllEnabledByUser(user);
    verify(mock, times(2)).getAllEnabledByUser(user);
  }

  @Test
  void close() {
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    service.getAllEnabledByUser(user);
    service.close(1);
    service.getAllEnabledByUser(user);
    verify(mock, times(2)).getAllEnabledByUser(user);
  }

  @Test
  void addMoney() {
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    service.getAllEnabledByUser(user);
    operationService.addMoney(1, 100);
    service.getAllEnabledByUser(user);
    verify(mock, times(2)).getAllEnabledByUser(user);
  }

  @Test
  void withdrawMoney() {
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    service.getAllEnabledByUser(user);
    operationService.withdrawMoney(1, 100);
    service.getAllEnabledByUser(user);
    verify(mock, times(2)).getAllEnabledByUser(user);
  }

  @Test
  void transferMoney() {
    assertIterableEquals(exceptedCards, service.getAllEnabledByUser(user));
    service.getAllEnabledByUser(user);
    operationService.transferMoney(1, "12", 100);
    service.getAllEnabledByUser(user);
    verify(mock, times(2)).getAllEnabledByUser(user);
  }
}
