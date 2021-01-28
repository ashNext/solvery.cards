package solvery.cards.service;

import org.springframework.context.support.MessageSourceAccessor;
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

  private final MessageSourceAccessor messageSourceAccessor;

  public UserService(UserRepository repository,
                     PasswordEncoder passwordEncoder,
                     MessageSourceAccessor messageSourceAccessor) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.messageSourceAccessor = messageSourceAccessor;
  }

  public User create(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setEnabled(true);
    return repository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = repository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException(
          messageSourceAccessor.getMessage("user.userNameNotFound"));
    }
    return user;
  }
}
