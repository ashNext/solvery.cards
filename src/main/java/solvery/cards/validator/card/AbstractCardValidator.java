package solvery.cards.validator.card;

import org.springframework.beans.factory.annotation.Autowired;
import solvery.cards.repository.CardRepository;

public abstract class AbstractCardValidator {

  @Autowired
  protected CardRepository repository;
}
