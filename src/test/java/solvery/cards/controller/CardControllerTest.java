package solvery.cards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import solvery.cards.dto.CardTo;
import solvery.cards.model.Card;
import solvery.cards.service.CardService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler.EXCEPTION_DUPLICATE_CARD;

//@WithMockUser(authorities = "u1")
@WithUserDetails(value = "u1")
@Sql(
    value = {"/create-users-before.sql", "/create-cards-before.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/create-cards-after.sql", "/create-users-after.sql"},
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CardControllerTest extends AbstractControllerTest {

  private static final List<Card> cards = List.of(
      new Card(1, user1, "11", 0, true),
      new Card(2, user1, "12", 0, true),
      new Card(3, user1, "13", 0, true)
  );

  @Autowired
  private CardService service;

  @Test
  @SuppressWarnings(value = "unchecked")
  void getAllEnabled() throws Exception {
    MvcResult result = mockMvc.perform(get("/card")
//        .with(user("u1"))
//        .with(httpBasic("u1", "1"))
    )
        .andDo(print())
        .andExpect(authenticated().withUsername("u1"))
        .andExpect(status().isOk())
        .andExpect(view().name("card"))
//        .andExpect(model().attribute("cards", equalTo(cards)))
        .andExpect(model().attribute("cardTo", equalTo(new CardTo())))
        .andExpect(content().string(containsString("Cards")))
        .andExpect(xpath("//div[@id='card-list']/table/tbody/tr").nodeCount(3))
        .andExpect(xpath("//div[@id='card-list']/table/tbody/tr[@id='card-list-1']/td[text()='11']")
            .exists())
        .andExpect(xpath("//div[@id='card-list']/table/tbody/tr[@id='card-list-2']/td[text()='12']")
            .exists())
        .andExpect(xpath("//div[@id='card-list']/table/tbody/tr[@id='card-list-3']/td[text()='13']")
            .exists())
        .andReturn();

    List<Card> actualCard = (List<Card>) Objects.requireNonNull(result.getModelAndView()).getModel()
        .get("cards");
    assertThat(actualCard).usingRecursiveComparison().ignoringFields("user.password")
        .isEqualTo(cards);
  }

  @Test
  @WithUserDetails(value = "u3")
  void getAllEnabled_noEnabledCards() throws Exception {
    mockMvc.perform(get("/card"))
        .andDo(print())
        .andExpect(authenticated().withUsername("u3"))
        .andExpect(status().isOk())
        .andExpect(view().name("card"))
        .andExpect(model().attribute("cards", empty()))
        .andExpect(model().attribute("cardTo", equalTo(new CardTo())))
        .andExpect(content().string(containsString("Cards")))
        .andExpect(xpath("//div[@id='card-list']").doesNotExist());
  }

  @Test
  void create() throws Exception {
    Card exceptedCard = new Card(9, user1, "100", 0, true);
    mockMvc.perform(post("/card")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("numb", "100")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(authenticated().withUsername("u1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/card"))
        .andExpect(view().name("redirect:/card"));

    Card actualCard = service.getEnabledByCardNumb("100");
    assertThat(actualCard).usingRecursiveComparison().ignoringFields("user.password")
        .isEqualTo(exceptedCard);
  }

  @Test
  void create_validationDuplicate() throws Exception {
    mockMvc.perform(post("/card")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("numb", "11")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(authenticated().withUsername("u1"))
        .andExpect(status().isOk())
        .andExpect(view().name("card"))
        .andExpect(model().attributeHasFieldErrorCode("cardTo", "numb", "UniqueCardNumber"))
        .andExpect(content().string(containsString(getMessage(EXCEPTION_DUPLICATE_CARD))));
  }

  @Test
  void close() throws Exception {
    mockMvc.perform(post("/card/close/2")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(authenticated().withUsername("u1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/card"))
        .andExpect(view().name("redirect:/card"));

    List<Card> enabledCards = cards.stream()
        .filter(card -> card.getId() != 2)
        .collect(Collectors.toList());

    assertThat(service.getAllEnabledByUser(user1)).usingRecursiveComparison()
        .ignoringFields("user.password")
        .isEqualTo(enabledCards);
  }
}