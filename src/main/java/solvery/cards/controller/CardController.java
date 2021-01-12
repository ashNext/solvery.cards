package solvery.cards.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.service.CardService;

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
    return "/card";
  }

  @PostMapping
  public String create(@AuthenticationPrincipal User user, Card card) {
    card.setUser(user);
    service.create(card);
    return "redirect:/card";
  }

  @PostMapping("/delete/{id}")
  public String close(@PathVariable Integer id) {
    Card card = service.getById(id);
    card.setEnabled(false);
    service.update(card);
    return "redirect:/card";
  }
}
