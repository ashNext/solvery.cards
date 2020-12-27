package solvery.cards.service;

import java.util.List;
import org.springframework.stereotype.Service;
import solvery.cards.model.Card;
import solvery.cards.repository.CardRepository;

@Service
public class CardService {
  private final CardRepository repository;

  public CardService(CardRepository repository) {
    this.repository = repository;
  }

  public List<Card> getAll(){
    return repository.findAll();
  }

  public Card create(Card card){
    return repository.save(card);
  }

  public void delete(Integer id){
    repository.deleteById(id);
  }
}
