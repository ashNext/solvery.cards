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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.dto.CardTo;
import solvery.cards.model.Card;
import solvery.cards.model.Role;
import solvery.cards.model.User;
import solvery.cards.service.CardService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
//@WithMockUser(authorities = "u1")
@Sql(
    value = {"/create-users-before.sql", "/create-cards-before.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/create-cards-after.sql", "/create-users-after.sql"},
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CardControllerTest {

  private static final Locale RU_LOCALE = new Locale("ru");

  private static final User user =
      new User(1, "u1", "@@", "user1", "user1@a.ru", Collections.singleton(Role.USER), true);

  private static final List<Card> cards = List.of(
      new Card(1, user, "11", 0, true),
      new Card(2, user, "12", 0, true),
      new Card(3, user, "13", 0, true)
  );

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CardService service;

  @Autowired
  private MessageSourceAccessor messageSourceAccessor;

  @Test
  @WithUserDetails(value = "u1")
  @Transactional
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
  @WithUserDetails(value = "u1")
  @Transactional
  void create() throws Exception {
    Card exceptedCard = new Card(9, user, "100", 0, true);
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
  @WithUserDetails(value = "u1")
  @Transactional
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
  @WithUserDetails(value = "u1")
  @Transactional
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

    assertThat(service.getAllEnabledByUser(user)).usingRecursiveComparison()
        .ignoringFields("user.password")
        .isEqualTo(enabledCards);
  }

  private String getMessage(String code) {
    return messageSourceAccessor.getMessage(code, RU_LOCALE);
  }
}