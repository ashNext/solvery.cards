package solvery.cards.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import solvery.cards.config.EncoderConfig;
import solvery.cards.dto.UserRegistrationTo;
import solvery.cards.service.UserService;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-users-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-users-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

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
        .param("username", "u3")
        .param("fullName", "user3")
        .param("email", "user3@b.ru")
        .param("password", "1")
        .param("confirmPassword", "1")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"))
        .andExpect(view().name("redirect:/login"));
  }
}