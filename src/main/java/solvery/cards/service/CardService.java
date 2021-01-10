package solvery.cards.service;

import org.springframework.stereotype.Service;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.repository.CardRepository;

import java.util.List;

@Service
public class CardService {

  private final CardRepository repository;

  public CardService(CardRepository repository) {
    this.repository = repository;
  }

  public List<Card> getAllEnabledByUser(User user) {
    return repository.getAllByUser(user, true);
  }

  public Card create(Card card) {
    card.setBalance(0);
    card.setEnabled(true);
    return repository.save(card);
  }

  public void update(Card card) {
    repository.save(card);
  }

  public Card getById(Integer id) {
    return repository.findById(id).orElse(null);
  }

  public Card getByCardNumb(String cardNumb) {
    return repository.findByNumb(cardNumb).orElse(null);
  }

  public void refreshBalanceByCardNumb(String cardNumb, Integer balance) {
    Card card = getByCardNumb(cardNumb);
    card.setBalance(balance);
    update(card);
  }
}
