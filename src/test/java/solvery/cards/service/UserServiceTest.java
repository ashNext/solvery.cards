package solvery.cards.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.repository.UserRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class UserServiceTest {

  @Mock
  private UserRepository repository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService service;

  private final User exceptedUser =
      new User(1, "u1", "@@", "user1", "a@b.ru",
          Collections.singleton(Role.USER), true);

  @Test
  void create() {
    when(repository.save(exceptedUser)).thenReturn(exceptedUser);

    User user =
        new User(1, "u1", "1", "user1", "a@b.ru", Collections.singleton(Role.USER));
    when(passwordEncoder.encode("1")).thenReturn("@@");
    User actualUser = service.create(user);

    assertEquals(exceptedUser, actualUser);

    verify(passwordEncoder).encode("1");
    verify(repository).save(exceptedUser);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void create_duplicateUsername() {
    User newUser = new User(2, "u1", "1", "user2", "b@c.ru");
    when(repository.save(newUser)).thenThrow(new DataIntegrityViolationException(""));

    assertThrows(DataIntegrityViolationException.class, () -> service.create(newUser));
  }

  @Test
  void create_duplicateEmail() {
    User newUser = new User(2, "u2", "1", "user2", "a@b.ru");
    when(repository.save(newUser)).thenThrow(new DataIntegrityViolationException(""));

    assertThrows(DataIntegrityViolationException.class, () -> service.create(newUser));
  }

  @Test
  void loadUserByUsername() {
    when(repository.findByUsername("u1")).thenReturn(exceptedUser);

    UserDetails actualUser = service.loadUserByUsername("u1");
    assertEquals(exceptedUser, actualUser);

    verify(repository).findByUsername("u1");
    verifyNoMoreInteractions(repository);
  }

  @Test
  void loadUserByUsername_notFound() {
    when(repository.findByUsername(anyString())).thenThrow(new UsernameNotFoundException(""));

    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("u1"));
  }
}