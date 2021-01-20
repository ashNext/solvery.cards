package solvery.cards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvery.cards.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  User findByUsername(String login);

  User getByEmail(String email);
}
