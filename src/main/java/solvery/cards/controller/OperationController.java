package solvery.cards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solvery.cards.service.OperationService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/operation")
public class OperationController {

  private final OperationService service;

  public OperationController(OperationService service) {
    this.service = service;
  }

  @GetMapping("/{cardId}")
  public String get(@PathVariable Integer cardId, Model model) {
    model.addAttribute("cardId", cardId);
    return "/operation";
  }

  @PostMapping("/{cardId}")
  public String addMoney(@PathVariable Integer cardId, @RequestParam String sum) {
    service.addMoney(cardId, Integer.valueOf(sum));
    return "redirect:/operation/" + cardId;
  }

  @PostMapping("/{cardId}/transfer")
  public String transferMoney(
          @PathVariable Integer cardId,
          @RequestParam String recipientCardNumber,
          @RequestParam String sum) {
    service.transferMoney(cardId, recipientCardNumber, Integer.valueOf(sum));
    return "redirect:/operation/" + cardId;
  }

  @GetMapping("/{cardId}/history")
  public String getHistory(
          @PathVariable Integer cardId,
          @RequestParam String recipientCardNumber,
          @RequestParam String startDate,
          @RequestParam String endDate,
          Model model) {
    model.addAttribute(
            "operations",
            service.getByFilter(
                    cardId,
                    recipientCardNumber.isBlank() ? null : recipientCardNumber,
                    startDate.isBlank() ? LocalDateTime.of(1, 1, 1, 0, 0) : LocalDateTime.parse(startDate),
                    endDate.isBlank() ? LocalDateTime.of(3000, 1, 1, 0, 0) : LocalDateTime.parse(endDate)));
    return "/operation";
  }
}
