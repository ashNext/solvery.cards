package solvery.cards.service.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.repository.UserRepository;
import solvery.cards.service.UserService;
import solvery.cards.service.UserServiceInterfaceTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceUnitTest extends AbstractServiceUnitTest implements UserServiceInterfaceTest {

  @Mock
  private UserRepository repository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService service;

  @Test
  public void create() {
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
  @Override
  public void createShouldReturnDuplicateUsername() {
    User newUser = new User(2, "u1", "1", "user2", "b@c.ru");
    when(repository.save(newUser)).thenThrow(new DataIntegrityViolationException("42"));

    DataIntegrityViolationException exception =
        assertThrows(DataIntegrityViolationException.class, () -> service.create(newUser));
    assertEquals("42", exception.getMessage());
  }

  @Test
  @Override
  public void createShouldReturnDuplicateEmail() {
    User newUser = new User(2, "u2", "1", "user2", "a@b.ru");
    when(repository.save(newUser)).thenThrow(new DataIntegrityViolationException("42"));

    DataIntegrityViolationException exception =
        assertThrows(DataIntegrityViolationException.class, () -> service.create(newUser));
    assertEquals("42", exception.getMessage());
  }

  @Test
  @Override
  public void loadUserByUsername() {
    when(repository.findByUsername("u1")).thenReturn(exceptedUser);

    UserDetails actualUser = service.loadUserByUsername("u1");
    assertEquals(exceptedUser, actualUser);

    verify(repository).findByUsername("u1");
    verifyNoMoreInteractions(repository);
  }

  @Test
  @Override
  public void loadUserByUsernameShouldReturnNotFound() {
    when(repository.findByUsername("u1")).thenReturn(null);
    when(messageSourceAccessor.getMessage(
        eq("user.userNameNotFound"),
        eq(LocaleContextHolder.getLocale()))
    ).thenReturn("42");

    UsernameNotFoundException exception =
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("u1"));
    assertEquals("42", exception.getMessage());
  }
}