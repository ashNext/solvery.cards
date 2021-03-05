package solvery.cards.service;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.User;
import solvery.cards.repository.UserRepository;

@Service
@Transactional(readOnly = true)
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

  @Transactional
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
          messageSourceAccessor.getMessage("user.userNameNotFound", LocaleContextHolder.getLocale()));
    }
    return user;
  }
}
