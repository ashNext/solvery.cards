package solvery.cards.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler.EXCEPTION_DUPLICATE_EMAIL;
import static solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler.EXCEPTION_DUPLICATE_USERNAME;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import solvery.cards.dto.UserRegistrationTo;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest {

  private static final Locale RU_LOCALE = new Locale("ru");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MessageSourceAccessor messageSourceAccessor;

  @Test
  void registration() throws Exception {
    mockMvc.perform(get("/registration"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("registration"))
        .andExpect(model().attribute("userRegistrationTo", equalTo(new UserRegistrationTo())))
        .andExpect(content().string(containsString("Registration")));
  }

  @Test
  void create() throws Exception {
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
  }

  @Test
  void create_validateDuplicateAndNoMatchRetypePassword() throws Exception {
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
        .andExpect(content().string(containsString(getMessage("user.matchRetypePassword"))))
        .andExpect(status().isOk())
        .andExpect(view().name("registration"));
  }

  private String getMessage(String code) {
    return messageSourceAccessor.getMessage(code, RU_LOCALE);
  }
}