package solvery.cards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.Card;
import solvery.cards.model.User;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
public interface CardRepository extends JpaRepository<Card, Integer> {

  @Query("SELECT c FROM Card c WHERE c.user=:user AND c.enabled=:enabled ORDER BY c.id")
  List<Card> getAllByUserAndEnabled(User user, boolean enabled);

  @Query("SELECT c FROM Card c WHERE c.user=:user ORDER BY c.id")
  List<Card> getAllByUser(User user);

  Optional<Card> findByNumb(String numb);

  Optional<Card> findByNumbAndEnabledTrue(String numb);

  Optional<Card> findByIdAndEnabledTrue(Integer id);

  Optional<Card> findByIdAndEnabledFalse(Integer id);
}
