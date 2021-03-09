package solvery.cards.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.UserService;
import solvery.cards.service.UserServiceInterfaceTest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Sql(value = {"/create-users-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserServiceIntegrationTest extends AbstractServiceIntegrationTest implements UserServiceInterfaceTest {

  @Autowired
  private UserService service;

  @Test
  @Override
  public void create() {
    User newUser =
        new User("u0", "psw", "user0", "user0@a.ru", Collections.singleton(Role.USER));

    User created = service.create(newUser);
    newUser.setId(created.getId());

    assertThat(created).usingRecursiveComparison().ignoringFields("password").isEqualTo(newUser);
    assertEquals(service.loadUserByUsername(newUser.getUsername()), newUser);
  }

  @Test
  @Override
  public void createShouldReturnDuplicateUsername() {
    User newUser =
        new User("u1", "psw", "user0", "user0@a.ru", Collections.singleton(Role.USER));

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> service.create(newUser))
        .havingRootCause()
        .withMessageContaining("users_unique_username_idx");
  }

  @Test
  @Override
  public void createShouldReturnDuplicateEmail() {
    User newUser =
        new User("u0", "psw", "user0", "user1@a.ru", Collections.singleton(Role.USER));

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> service.create(newUser))
        .havingRootCause()
        .withMessageContaining("users_unique_email_idx");
  }

  @Test
  @Override
  public void loadUserByUsername() {
    User exceptedUser =
        new User(1, "u1", "psw", "user1", "user1@a.ru", Collections.singleton(Role.USER), true);

    UserDetails actualUser = service.loadUserByUsername("u1");

    assertThat(actualUser).usingRecursiveComparison().ignoringFields("password").isEqualTo(exceptedUser);
  }

  @Test
  @Override
  public void loadUserByUsernameShouldReturnNotFound() {
    UsernameNotFoundException exception =
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("u0"));
    assertEquals(messageSourceAccessor.getMessage("user.userNameNotFound"), exception.getMessage());
  }
}