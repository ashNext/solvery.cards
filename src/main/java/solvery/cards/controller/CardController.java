package solvery.cards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import solvery.cards.model.Card;
import solvery.cards.service.CardService;

@Controller
public class CardController {

  private final CardService service;

  public CardController(CardService service) {
    this.service = service;
  }

  @GetMapping("/hello")
  public String getAll(Model model){
    model.addAttribute("cards", service.getAll());
    return "/hello";
  }

  @PostMapping("/hello")
  public String create(Card card){
    card.setBalance(0);
    service.create(card);
    return "redirect:/hello";
  }

  @GetMapping("/hello/delete/{id}")
  public String delete(@PathVariable Integer id){
    service.delete(id);
    return "redirect:/hello";
  }
}
