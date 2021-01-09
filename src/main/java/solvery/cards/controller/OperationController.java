package solvery.cards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import solvery.cards.service.OperationService;

import java.time.LocalDateTime;

@Controller
public class OperationController {

  private final OperationService service;

  public OperationController(OperationService service) {
    this.service = service;
  }

  @GetMapping("/operation/{cardNumb}")
  public String get(@PathVariable String cardNumb, Model model) {
    model.addAttribute("cardNumb", cardNumb);
    return "/operation";
  }

  @PostMapping("/operation/{cardNumb}")
  public String addMoney(@PathVariable String cardNumb, @RequestParam String sum) {
    service.addMoney(cardNumb, Integer.valueOf(sum));
    return "redirect:/operation/" + cardNumb;
  }

  @GetMapping("/operation/{cardNumb}/refresh")
  public String refreshBalance(@PathVariable String cardNumb) {
    service.refreshBalance(cardNumb);
    return "redirect:/operation/" + cardNumb;
  }

  @PostMapping("/operation/{cardNumb}/transfer")
  public String transferMoney(
          @PathVariable String cardNumb,
          @RequestParam String recipientCardNumber,
          @RequestParam String sum) {
    service.transferMoney(cardNumb, recipientCardNumber, Integer.valueOf(sum));
    return "redirect:/operation/" + cardNumb;
  }

  @GetMapping("/operation/{cardNumb}/history")
  public String getHistory(
          @PathVariable String cardNumb,
          @RequestParam String recipientCardNumber,
          @RequestParam String startDate,
          @RequestParam String endDate,
          Model model) {
    model.addAttribute(
            "operations",
            service.getByFilter(
                    cardNumb,
                    startDate.isBlank() ? LocalDateTime.of(1, 1, 1, 0, 0) : LocalDateTime.parse(startDate),
                    endDate.isBlank() ? LocalDateTime.of(3000, 1, 1, 0, 0) : LocalDateTime.parse(endDate)));
    return "/operation";
  }
}
