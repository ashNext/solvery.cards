package solvery.cards.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solvery.cards.dto.CardDTO;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.service.CardService;

@Controller
@RequestMapping(value = "/card")
public class CardController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final CardService cardService;

  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @GetMapping
  public String getAllEnabled(@AuthenticationPrincipal User user, Model model) {
    logger.info("getAll cards for user {}", user.getUsername());
    model.addAttribute("cards", cardService.getAllByUser(user));
    model.addAttribute("cardDTO", new CardDTO());
    return "card";
  }

  @PostMapping
  public String create(
      @AuthenticationPrincipal User user,
      @Valid CardDTO cardDTO,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", cardService.getAllByUser(user));
      return "card";
    }
    Card card = new Card(user, cardDTO.getNumb());
    logger.info("create card {} for user {}", card, user.getUsername());
    cardService.create(card);
    return "redirect:/card";
  }

  @PostMapping("/{id}/close")
  public String close(@PathVariable Integer id) {
    logger.info("close cardId {}", id);
    cardService.close(id);
    return "redirect:/card";
  }

  @PostMapping("/{id}/open")
  public String openBack(@PathVariable Integer id) {
    logger.info("openBack cardId {}", id);
    cardService.openBack(id);
    return "redirect:/card";
  }
}
