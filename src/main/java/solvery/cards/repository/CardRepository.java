package solvery.cards.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import solvery.cards.model.Card;
import solvery.cards.model.User;

public interface CardRepository extends JpaRepository<Card, Integer> {

  List<Card> getAllByUser(User user);
}
