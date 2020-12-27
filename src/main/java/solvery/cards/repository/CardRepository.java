package solvery.cards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvery.cards.model.Card;

public interface CardRepository extends JpaRepository<Card, Integer> {

}
