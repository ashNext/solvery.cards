package solvery.cards.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import solvery.cards.config.EncoderConfig;
import solvery.cards.dto.UserRegistrationTo;
import solvery.cards.service.UserService;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void registration() throws Exception {
    mockMvc.perform(get("/registration").requestAttr("userRegistrationTo", new UserRegistrationTo()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Registration")));
  }

  @Test
  void create() throws Exception {
    MvcResult result = mockMvc.perform(post("/registration")
    )
        .andDo(print()).andReturn();
//        .andExpect(status().is3xxRedirection());

    result.getModelAndView();
  }
}