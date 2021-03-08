package solvery.cards.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.Card;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.repository.CardRepository;
import solvery.cards.util.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class CardService {

  private final CardRepository repository;

  public CardService(CardRepository repository) {
    this.repository = repository;
  }

  @Cacheable(value = "cards")
  public List<Card> getAllByUser(User user) {
    if (user.getRoles().contains(Role.USER_ADVANCED)) {
      return repository.getAllByUser(user);
    } else {
      return repository.getAllByUserAndEnabled(user, true);
    }
  }

  @Cacheable(value = "cardsEnabled")
  public List<Card> getAllEnabledByUser(User user) {
    if (user.getRoles().contains(Role.USER_ADVANCED)) {
      return getAllByUser(user).stream()
          .filter(Card::isEnabled)
          .collect(Collectors.toList());
    } else {
      return getAllByUser(user);
    }
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public Card create(Card card) {
    card.setBalance(0);
    card.setEnabled(true);
    return repository.save(card);
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public void update(Card card) {
    repository.save(card);
  }

  public Card getById(Integer id) {
    return repository.findById(id)
        .orElseThrow(() -> new NotFoundException("card.cardByIdNotFound"));
  }

  public Card getEnabledById(Integer id) {
    return repository.findByIdAndEnabledTrue(id)
        .orElseThrow(() -> new NotFoundException("card.cardByIdNotFound"));
  }

  public Card getDisabledById(Integer id) {
    return repository.findByIdAndEnabledFalse(id)
        .orElseThrow(() -> new NotFoundException("card.cardByIdNotFound"));
  }

  public Card getEnabledByCardNumb(String cardNumb) {
    return repository.findByNumbAndEnabledTrue(cardNumb)
        .orElseThrow(() -> new NotFoundException("card.numbNotFound", cardNumb));
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public Card close(Integer id) {
    Card card = getEnabledById(id);
    card.setEnabled(false);
    update(card);
    return card;
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public Card openBack(Integer id) {
    Card card = getDisabledById(id);
    card.setEnabled(true);
    update(card);
    return card;
  }
}
