package solvery.cards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import solvery.cards.dto.UserRegistrationDTO;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.UserService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler.*;

@Sql(value = {"/create-users-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest extends AbstractControllerTest {

  @Autowired
  private UserService userService;

  @Test
  void registration() throws Exception {
    mockMvc.perform(get("/registration"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("registration"))
        .andExpect(model().attribute("userRegistrationDTO", equalTo(new UserRegistrationDTO())))
        .andExpect(content().string(containsString("Registration")));
  }

  @Test
  void create() throws Exception {
    User exceptedUser = new User(5, "u0", "@", "user0", "user0@b.ru",
        Set.of(Role.USER), true);
    mockMvc.perform(post("/registration")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", "u0")
        .param("fullName", "user0")
        .param("email", "user0@b.ru")
        .param("password", "1")
        .param("confirmPassword", "1")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"))
        .andExpect(view().name("redirect:/login"));

    User actualUser = (User) userService.loadUserByUsername("u0");
    assertThat(actualUser).usingRecursiveComparison().ignoringFields("password").isEqualTo(exceptedUser);
  }

  @Test
  void createAdvancedUser() throws Exception {
    User exceptedUser = new User(5, "au0", "@", "auser0", "auser0@b.ru",
        Set.of(Role.USER, Role.USER_ADVANCED), true);
    mockMvc.perform(post("/registration")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", "au0")
        .param("fullName", "auser0")
        .param("email", "auser0@b.ru")
        .param("password", "1")
        .param("confirmPassword", "1")
        .param("advanced", "true")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"))
        .andExpect(view().name("redirect:/login"));

    User actualUser = (User) userService.loadUserByUsername("au0");
    assertThat(actualUser).usingRecursiveComparison().ignoringFields("password").isEqualTo(exceptedUser);
  }

  @Test
  void validateOnCreateDuplicateAndNoMatchRetypePassword() throws Exception {
    mockMvc.perform(post("/registration")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", "u1")
        .param("fullName", "user3")
        .param("email", "user1@a.ru")
        .param("password", "1")
        .param("confirmPassword", "2")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(content().string(containsString(getMessage(EXCEPTION_DUPLICATE_USERNAME))))
        .andExpect(content().string(containsString(getMessage(EXCEPTION_DUPLICATE_EMAIL))))
        .andExpect(content().string(containsString(getMessage(EXCEPTION_NO_MATCH_RETYPE_PASSWORD))))
        .andExpect(status().isOk())
        .andExpect(view().name("registration"));
  }
}