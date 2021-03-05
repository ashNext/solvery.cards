package solvery.cards.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import solvery.cards.dto.OperationCashDTO;
import solvery.cards.dto.OperationTransferDTO;
import solvery.cards.model.Card;
import solvery.cards.model.User;
import solvery.cards.service.CardService;
import solvery.cards.service.OperationService;
import solvery.cards.util.OperationUtil;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/operation")
public class OperationController {

  private final OperationService operationService;

  private final CardService cardService;

  private final MessageSourceAccessor messageSourceAccessor;

  public OperationController(OperationService operationService, CardService cardService, MessageSourceAccessor messageSourceAccessor) {
    this.operationService = operationService;
    this.cardService = cardService;
    this.messageSourceAccessor = messageSourceAccessor;
  }

  @GetMapping
  public String get() {
    return "/operation";
  }

  @GetMapping("/result")
  public String getResultOperation(@RequestParam @Nullable String type, @RequestParam String status, Model model) {
    String resultStatus =
        messageSourceAccessor.getMessage("operation.result.status.bad", LocaleContextHolder.getLocale());
    if ("ok".equalsIgnoreCase(status)) {
      resultStatus =
          messageSourceAccessor.getMessage("operation.result.status.ok", LocaleContextHolder.getLocale());
    }

    String message = "";
    if ("add".equalsIgnoreCase(type)) {
      message = messageSourceAccessor.getMessage("operation.result.deposit.ok", LocaleContextHolder.getLocale());
    } else if ("withdraw".equalsIgnoreCase(type)) {
      message = messageSourceAccessor.getMessage("operation.result.withdraw.ok", LocaleContextHolder.getLocale());
    } else if ("transfer".equalsIgnoreCase(type)) {
      message = messageSourceAccessor.getMessage("operation.result.transfer.ok", LocaleContextHolder.getLocale());
    }

    model.addAttribute("resultStatus", resultStatus);
    model.addAttribute("resultMessage", message);
    return "/operations/result";
  }

  @GetMapping("/add")
  public String getAddMoney(@AuthenticationPrincipal User user, Model model) {
    model.addAttribute("operationCashDTO", new OperationCashDTO());
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    return "/operations/add";
  }

  @PostMapping("/add")
  public String addMoney(
      @AuthenticationPrincipal User user,
      @Valid OperationCashDTO operationCashDTO,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", cardService.getAllEnabledByUser(user));
      return "/operations/add";
    }
    operationService.addMoney(operationCashDTO.getCard().getId(), operationCashDTO.getSum());
    return "redirect:/operation/result?type=add&status=ok";
  }

  @GetMapping("/withdraw")
  public String getWithdrawMoney(@AuthenticationPrincipal User user, Model model) {
    model.addAttribute("operationCashDTO", new OperationCashDTO());
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    return "/operations/withdraw";
  }

  @PostMapping("/withdraw")
  public String withdrawMoney(
      @AuthenticationPrincipal User user,
      @Valid OperationCashDTO operationCashDTO,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", cardService.getAllEnabledByUser(user));
      return "/operations/withdraw";
    }
    operationService.withdrawMoney(operationCashDTO.getCard().getId(), operationCashDTO.getSum());
    return "redirect:/operation/result?type=withdraw&status=ok";
  }

  @GetMapping("/transfer")
  public String getTransfer(@AuthenticationPrincipal User user, Model model) {
    model.addAttribute("operationTransferDTO", new OperationTransferDTO());
    model.addAttribute("cards", cardService.getAllEnabledByUser(user));
    return "/operations/transfer";
  }

  @PostMapping("/transfer")
  public String transferMoney(
      @AuthenticationPrincipal User user,
      @Valid OperationTransferDTO operationTransferDTO,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("cards", cardService.getAllEnabledByUser(user));
      return "/operations/transfer";
    }

    operationService.transferMoney(
        cardService.getEnabledByCardNumb(operationTransferDTO.getCardNumb()).getId(),
        operationTransferDTO.getRecipientCardNumb(),
        operationTransferDTO.getSum());
    return "redirect:/operation/result?type=transfer&status=ok";
  }

  @GetMapping("/history")
  public String getHistory(
      @AuthenticationPrincipal User user,
      @RequestParam @Nullable Integer cardId,
      @RequestParam @Nullable String recipientCardNumber,
      @RequestParam(defaultValue = "0") Integer directionId,
      @RequestParam(defaultValue = "0") Integer typeId,
      @RequestParam @Nullable LocalDate startDate,
      @RequestParam @Nullable LocalDate endDate,
      Model model) {
    List<Card> cards = cardService.getAllByUser(user);
    model.addAttribute("cards", cards);
    model.addAttribute("directionSelected", directionId);
    model.addAttribute("typeSelected", typeId);

    if (cardId == null) {
      cardId = !cards.isEmpty() ? cards.get(0).getId() : null;
    }
    model.addAttribute("cardSelected", cardId);

    if (cardId != null) {
      model.addAttribute("operations",
          OperationUtil.getListOperationHistoryTo(
              operationService.getByFilter(cardId, recipientCardNumber, directionId, typeId, startDate, endDate)));
    }
    return "/operations/history";
  }
}
