package solvery.cards.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.repository.CardRepository;
import solvery.cards.util.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CardService {

  private final CardRepository repository;

  public CardService(CardRepository repository) {
    this.repository = repository;
  }

  @Cacheable(value = "cards")
  public List<Card> getAllEnabledByUser(User user) {
    return repository.getAllByUser(user, true);
  }

  @Transactional
  @CacheEvict(value = "cards", allEntries = true)
  public Card create(Card card) {
    card.setBalance(0);
    card.setEnabled(true);
    return repository.save(card);
  }

  @Transactional
  @CacheEvict(value = "cards", allEntries = true)
  public void update(Card card) {
    repository.save(card);
  }

  public Card getEnabledById(Integer id) {
    return repository.findByIdAndEnabledTrue(id)
        .orElseThrow(() -> new NotFoundException("Card is not found!"));
  }

  public Card getEnabledByCardNumb(String cardNumb) {
    return repository.findByNumbAndEnabledTrue(cardNumb)
        .orElseThrow(() -> new NotFoundException("Card \"" + cardNumb + "\" is not found!"));
  }

  @Transactional
  @CacheEvict(value = "cards", allEntries = true)
  public Card close(Integer id) {
    Card card = getEnabledById(id);
    card.setEnabled(false);
    update(card);
    return card;
  }
}
