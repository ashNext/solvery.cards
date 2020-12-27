package solvery.cards.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import solvery.cards.model.User;
import solvery.cards.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository repository;

  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  public User getByLogin(String login) {
    return repository.findByLogin(login);
  }

  public User create(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return repository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    return repository.findByLogin(s);
  }
}
