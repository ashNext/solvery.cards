package solvery.cards.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import solvery.cards.dto.OperationAddTo;
import solvery.cards.dto.OperationTransferTo;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.service.CardService;
import solvery.cards.service.OperationService;

import javax.validation.Valid;
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
      @RequestParam(required = false) Integer cardId,
      Model model) {
    if (cardId != null) {
      model.addAttribute("cardSelected", cardId);
    }

    model.addAttribute("operationAddTo", new OperationAddTo());
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    return "/operations/add";
  }

  @PostMapping("/add")
  public String addMoney(
      @AuthenticationPrincipal User user,
      @Valid OperationAddTo operationAddTo,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", cardService.getAllEnabledByUser(user));
      return "/operations/add";
    }
    service.addMoney(operationAddTo.getCard().getId(), operationAddTo.getSum());
    return "redirect:/operation/add?cardId=" + operationAddTo.getCard().getId();
  }

  @GetMapping("/transfer")
  public String getTransfer(
      @AuthenticationPrincipal User user,
      @RequestParam(required = false) Card card,
      Model model) {
    if (card != null) {
      model.addAttribute("cardSelected", card.getId());
    }

    model.addAttribute("operationTransferTo", new OperationTransferTo());
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    return "/operations/transfer";
  }

  @PostMapping("/transfer")
  public String transferMoney(
      @AuthenticationPrincipal User user,
      @Valid OperationTransferTo operationTransferTo,
      BindingResult bindingResult,
      Model model) {
    if (operationTransferTo.getCard().getNumb().equalsIgnoreCase(operationTransferTo.getRecipientCardNumb())) {
      bindingResult.addError(
          new FieldError(
              "recipientCardNumb",
              "recipientCardNumb",
              operationTransferTo.getRecipientCardNumb(),
              false,
              null,
              null,
              "Номер карты получателя не может совпадать с номером карты отправителя")
      );
    }

    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", cardService.getAllEnabledByUser(user));
      return "/operations/transfer";
    }

    service.transferMoney(
        operationTransferTo.getCard().getId(),
        operationTransferTo.getRecipientCardNumb(),
        operationTransferTo.getSum());
    return "redirect:/operation/transfer?cardId=" + operationTransferTo.getCard().getId();
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
