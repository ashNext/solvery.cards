package solvery.cards.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.service.CardService;

@Controller
public class CardController {

  private final CardService service;

  public CardController(CardService service) {
    this.service = service;
  }

  @GetMapping("/hello")
  public String getAll(@AuthenticationPrincipal User user, Model model){
    model.addAttribute("cards", service.getAllByUser(user));
    return "/hello";
  }

  @PostMapping("/hello")
  public String create(@AuthenticationPrincipal User user, Card card){
    card.setUser(user);
    card.setBalance(0);
    service.create(card);
    return "redirect:/hello";
  }

  @DeleteMapping("/hello/delete/{id}")
  public String delete(@PathVariable Integer id){
    service.delete(id);
    return "redirect:/hello";
  }
}
