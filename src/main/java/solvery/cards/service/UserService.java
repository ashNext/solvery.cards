package solvery.cards.service;

import org.springframework.stereotype.Service;
import solvery.cards.model.User;
import solvery.cards.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository repository;

  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public User getByLogin(String login) {
    return repository.findByLogin(login);
  }

  public User create(User user) {
    return repository.save(user);
  }
}
