package solvery.cards.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.service.CardService;
import solvery.cards.service.OperationService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/operation")
public class OperationController {

  private final OperationService service;

  private final CardService cardService;

  public OperationController(OperationService service, CardService cardService) {
    this.service = service;
    this.cardService = cardService;
  }

  @GetMapping
  public String get() {
    return "/operation";
  }

  @GetMapping("/add")
  public String getAddMoney(
      @AuthenticationPrincipal User user,
      @RequestParam(required = false) Card card,
      Model model) {
    if (card != null) {
      model.addAttribute("cardSelected", card.getId());
    }
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    return "/operations/add";
  }

  @PostMapping("/add")
  public String addMoney(@RequestParam Card card, @RequestParam Integer sum) {
    service.addMoney(card.getId(), sum);
    return "redirect:/operation/add?card=" + card.getId();
  }

  @GetMapping("/transfer")
  public String getTransfer(
      @AuthenticationPrincipal User user,
      @RequestParam(required = false) Card card,
      Model model) {
    if (card != null) {
      model.addAttribute("cardSelected", card.getId());
    }
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    return "/operations/transfer";
  }

  @PostMapping("/transfer")
  public String transferMoney(
      @RequestParam Card card,
      @RequestParam String recipientCardNumber,
      @RequestParam Integer sum) {
    service.transferMoney(card.getId(), recipientCardNumber, sum);
    return "redirect:/operation/transfer?card=" + card.getId();
  }

  @GetMapping("/history")
  public String getHistory(
      @AuthenticationPrincipal User user,
      @RequestParam(required = false) Card card,
      @RequestParam(defaultValue = "") String recipientCardNumber,
      @RequestParam(defaultValue = "") String startDate,
      @RequestParam(defaultValue = "") String endDate,
      Model model) {
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    if (card != null) {
      model.addAttribute("cardSelected", card.getId());
      model.addAttribute(
          "operations",
          service.getByFilter(
              card.getId(),
              recipientCardNumber.isBlank() ? null : recipientCardNumber,
              startDate.isBlank() ? LocalDateTime.of(1, 1, 1, 0, 0)
                  : LocalDateTime.parse(startDate),
              endDate.isBlank() ? LocalDateTime.of(3000, 1, 1, 0, 0)
                  : LocalDateTime.parse(endDate)));
    }
    return "/operations/history";
  }
}
