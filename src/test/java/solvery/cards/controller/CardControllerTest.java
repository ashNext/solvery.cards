package solvery.cards.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler.EXCEPTION_DUPLICATE_CARD;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import solvery.cards.dto.CardDTO;
import solvery.cards.model.Card;
import solvery.cards.service.CardService;

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
  private CardService cardService;

  @Test
  @SuppressWarnings(value = "unchecked")
  void getAllEnabled() throws Exception {
    MvcResult result = mockMvc.perform(get("/card"))
        .andDo(print())
        .andExpect(authenticated().withUsername("u1"))
        .andExpect(status().isOk())
        .andExpect(view().name("card"))
        .andExpect(model().attribute("cardDTO", equalTo(new CardDTO())))
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
        .andExpect(model().attribute("cardDTO", equalTo(new CardDTO())))
        .andExpect(content().string(containsString("Cards")))
        .andExpect(xpath("//div[@id='card-list']").doesNotExist());
  }

  @Test
  void create() throws Exception {
    Card exceptedCard = new Card(13, user1, "100", 0, true);
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

    Card actualCard = cardService.getEnabledByCardNumb("100");
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
        .andExpect(model().attributeHasFieldErrorCode("cardDTO", "numb", "UniqueCardNumber"))
        .andExpect(content().string(containsString(getMessage(EXCEPTION_DUPLICATE_CARD))));
  }

  @Test
  void close() throws Exception {
    mockMvc.perform(post("/card/2/close")
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

    assertThat(cardService.getAllByUser(user1)).usingRecursiveComparison()
        .ignoringFields("user.password")
        .isEqualTo(enabledCards);
  }

  @Test
  @WithUserDetails(value = "au4")
  void openBack() throws Exception {
    List<Card> exceptedCardsUser4 = List.of(
        new Card(9, user4, "41", 0, true),
        new Card(10, user4, "42", 0, true),
        new Card(11, user4, "43", 0, true),
        new Card(12, user4, "44", 0, false)
    );

    mockMvc.perform(post("/card/10/open")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(authenticated().withUsername("au4"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/card"))
        .andExpect(view().name("redirect:/card"));

    assertThat(cardService.getAllByUser(user4)).usingRecursiveComparison()
        .ignoringFields("user.password")
        .isEqualTo(exceptedCardsUser4);
  }

  @Test
  void openBackForbidden() throws Exception {
    mockMvc.perform(post("/card/4/open")
        .with(csrf())
    )
        .andDo(print())
        .andExpect(authenticated().withUsername("u1"))
        .andExpect(status().isForbidden());
  }
}