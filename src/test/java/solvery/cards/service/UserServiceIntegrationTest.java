package solvery.cards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import solvery.cards.model.Role;
import solvery.cards.model.User;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserServiceIntegrationTest {

  @Autowired
  private UserService service;

  @Test
  void create() {
    User newUser =
        new User("u0", "psw", "user0", "user0@a.ru", Collections.singleton(Role.USER));

    User created = service.create(newUser);
    newUser.setId(created.getId());

    assertThat(created).usingRecursiveComparison().ignoringFields("password").isEqualTo(newUser);
    assertEquals(service.loadUserByUsername(newUser.getUsername()), newUser);
  }

  @Test
  void create_duplicateUsername() {
    User newUser =
        new User("u1", "psw", "user0", "user0@a.ru", Collections.singleton(Role.USER));

    assertThrows(DataIntegrityViolationException.class, () -> service.create(newUser));
  }

  @Test
  void create_duplicateEmail() {
    User newUser = new User("u0", "psw", "user0", "user1@a.ru", Collections.singleton(Role.USER));

    assertThrows(DataIntegrityViolationException.class, () -> service.create(newUser));
  }

  @Test
  void loadUserByUsername() {
    User exceptedUser =
        new User(1, "u1", "psw", "user1", "user1@a.ru", Collections.singleton(Role.USER), true);

    UserDetails actualUser = service.loadUserByUsername("u1");

    assertThat(actualUser).usingRecursiveComparison().ignoringFields("password")
        .isEqualTo(exceptedUser);
  }

  @Test
  void loadUserByUsername_notFound() {
    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("u0"));
  }
}