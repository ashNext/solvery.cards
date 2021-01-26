package solvery.cards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import solvery.cards.model.User;
import solvery.cards.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository repository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService service;

  private final User exceptedUser = new User(1, "u1", "@@", "user1", "a@b.ru");

  @Test
  void getByUsername() {
    when(repository.findByUsername("u1")).thenReturn(exceptedUser);

    User actualUser = service.getByUsername("u1");
    assertEquals(exceptedUser, actualUser);

    verify(repository).findByUsername("u1");
    verifyNoMoreInteractions(repository);
  }

  @Test
  void create() {
    when(repository.save(exceptedUser)).thenReturn(exceptedUser);

    User user = new User(1, "u1", "1", "user1", "a@b.ru");
    when(passwordEncoder.encode("1")).thenReturn("@@");
    User actualUser = service.create(user);

    assertEquals(exceptedUser, actualUser);

    verify(passwordEncoder).encode("1");
    verify(repository).save(exceptedUser);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void create_duplicateUsername() {
    when(repository.save(exceptedUser)).thenReturn(exceptedUser);

    User user1 = new User(1, "u1", "1", "user1", "a@b.ru");
    when(passwordEncoder.encode("1")).thenReturn("@@");
    service.create(user1);

    User userDuplicateName = new User(2, "u1", "@@", "user2", "b@c.ru");
    when(repository.save(userDuplicateName)).thenThrow(new DataIntegrityViolationException(""));

    User user2 = new User(2, "u1", "1", "user2", "b@c.ru");
    when(passwordEncoder.encode("1")).thenReturn("@@");
    assertThrows(DataIntegrityViolationException.class, () -> service.create(user2));
  }

  @Test
  void create_duplicateEmail() {
    when(repository.save(exceptedUser)).thenReturn(exceptedUser);

    User user1 = new User(1, "u1", "1", "user1", "a@b.ru");
    when(passwordEncoder.encode("1")).thenReturn("@@");
    service.create(user1);

    User userDuplicateEmail = new User(2, "u2", "@@", "user2", "a@b.ru");
    when(repository.save(userDuplicateEmail)).thenThrow(new DataIntegrityViolationException(""));

    User user2 = new User(2, "u2", "1", "user2", "a@b.ru");
    when(passwordEncoder.encode("1")).thenReturn("@@");
    assertThrows(DataIntegrityViolationException.class, () -> service.create(user2));
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