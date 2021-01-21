package solvery.cards.controller;

import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import solvery.cards.dto.OperationAddTo;
import solvery.cards.dto.OperationTransferTo;
import solvery.cards.model.User;
import solvery.cards.service.CardService;
import solvery.cards.service.OperationService;

import javax.validation.Valid;
import java.time.LocalDate;

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

  @GetMapping("/result")
  public String getResultOperation(@RequestParam @Nullable String type, @RequestParam String status, Model model) {
    String resultStatus = "Операция не была проведена";
    if ("ok".equalsIgnoreCase(status)) {
      resultStatus = "Операция успешно завершена";
    }

    String message = "";
    if ("add".equalsIgnoreCase(type)) {
      message = "Средства зачислены на баланс карты";
    } else if ("transfer".equalsIgnoreCase(type)) {
      message = "Средства переведены получателю";
    }

    model.addAttribute("resultStatus", resultStatus);
    model.addAttribute("resultMessage", message);
    return "/operations/result";
  }

  @GetMapping("/add")
  public String getAddMoney(@AuthenticationPrincipal User user, Model model) {
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
    return "redirect:/operation/result?type=add&status=ok";
  }

  @GetMapping("/transfer")
  public String getTransfer(@AuthenticationPrincipal User user, Model model) {
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
    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", cardService.getAllEnabledByUser(user));
      return "/operations/transfer";
    }

    service.transferMoney(
        cardService.getEnabledByCardNumb(operationTransferTo.getCardNumb()).getId(),
        operationTransferTo.getRecipientCardNumb(),
        operationTransferTo.getSum());
    return "redirect:/operation/result?type=transfer&status=ok";
  }

  @GetMapping("/history")
  public String getHistory(
      @AuthenticationPrincipal User user,
      @RequestParam @Nullable Integer cardId,
      @RequestParam @Nullable String recipientCardNumber,
      @RequestParam @Nullable LocalDate startDate,
      @RequestParam @Nullable LocalDate endDate,
      Model model) {
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    if (cardId != null) {
      model.addAttribute("cardSelected", cardId);
      model.addAttribute("operations", service.getByFilter(cardId, recipientCardNumber, startDate, endDate));
    }
    return "/operations/history";
  }
}
