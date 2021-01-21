package solvery.cards.service;

import org.springframework.stereotype.Service;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.repository.CardRepository;
import solvery.cards.util.exception.NotFoundException;

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

  public Card getEnabledById(Integer id) {
    return repository.findByIdAndEnabledTrue(id)
        .orElseThrow(() -> new NotFoundException("Card is not found!"));
  }

  public Card getEnabledByCardNumb(String cardNumb) {
    return repository.findByNumbAndEnabledTrue(cardNumb)
        .orElseThrow(() -> new NotFoundException("Card \"" + cardNumb + "\" is not found!"));
  }
}
