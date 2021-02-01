package solvery.cards.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solvery.cards.dto.CardTo;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.service.CardService;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/card")
public class CardController {

  private final CardService service;

  public CardController(CardService service) {
    this.service = service;
  }

  @GetMapping
  public String getAllEnabled(@AuthenticationPrincipal User user, Model model) {
    model.addAttribute("cards", service.getAllEnabledByUser(user));
    model.addAttribute("cardTo", new CardTo());
    return "card";
  }

  @PostMapping
  public String create(
      @AuthenticationPrincipal User user,
      @Valid CardTo cardTo,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", service.getAllEnabledByUser(user));
      return "card";
    }

    service.create(new Card(user, cardTo.getNumb(), 0));
    return "redirect:/card";
  }

  @PostMapping("/{id}/close")
  public String close(@PathVariable Integer id) {
    service.close(id);
    return "redirect:/card";
  }
}
