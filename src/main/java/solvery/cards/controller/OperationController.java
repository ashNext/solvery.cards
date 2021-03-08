package solvery.cards.controller;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequestMapping("/operation")
public class OperationController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final OperationService operationService;

  private final CardService cardService;

  private final MessageSourceAccessor messageSourceAccessor;

  public OperationController(OperationService operationService, CardService cardService,
      MessageSourceAccessor messageSourceAccessor) {
    this.operationService = operationService;
    this.cardService = cardService;
    this.messageSourceAccessor = messageSourceAccessor;
  }

  @GetMapping
  public String get() {
    return "/operation";
  }

  @GetMapping("/result")
  public String getResultOperation(@RequestParam @Nullable String type, @RequestParam String status,
      Model model) {
    String resultStatus =
        messageSourceAccessor
            .getMessage("operation.result.status.bad", LocaleContextHolder.getLocale());
    if ("ok".equalsIgnoreCase(status)) {
      resultStatus =
          messageSourceAccessor
              .getMessage("operation.result.status.ok", LocaleContextHolder.getLocale());
    }

    String message = "";
    if ("add".equalsIgnoreCase(type)) {
      message = messageSourceAccessor
          .getMessage("operation.result.deposit.ok", LocaleContextHolder.getLocale());
    } else if ("withdraw".equalsIgnoreCase(type)) {
      message = messageSourceAccessor
          .getMessage("operation.result.withdraw.ok", LocaleContextHolder.getLocale());
    } else if ("transfer".equalsIgnoreCase(type)) {
      message = messageSourceAccessor
          .getMessage("operation.result.transfer.ok", LocaleContextHolder.getLocale());
    }

    model.addAttribute("resultStatus", resultStatus);
    model.addAttribute("resultMessage", message);
    return "/operations/result";
  }

  @GetMapping("/add")
  public String getAddMoney(@AuthenticationPrincipal User user, Model model) {
    List<Card> enabledCards = cardService.getAllEnabledByUser(user);
    logger.info("getAddMoney page with enabledCards {} for user {}", enabledCards,
        user.getUsername());
    model.addAttribute("operationCashDTO", new OperationCashDTO());
    model.addAttribute("cards", enabledCards);
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
    logger.info("create addMoney operation on cardId {} in amount {}",
        operationCashDTO.getCard().getId(), operationCashDTO.getSum());
    operationService.addMoney(operationCashDTO.getCard().getId(), operationCashDTO.getSum());
    return "redirect:/operation/result?type=add&status=ok";
  }

  @GetMapping("/withdraw")
  public String getWithdrawMoney(@AuthenticationPrincipal User user, Model model) {
    List<Card> enabledCards = cardService.getAllEnabledByUser(user);
    logger.info("getWithdrawMoney page with enabledCards {} for user {}", enabledCards,
        user.getUsername());
    model.addAttribute("operationCashDTO", new OperationCashDTO());
    model.addAttribute("cards", enabledCards);
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
    logger
        .info("create withdrawMoney operation on cardId {} in amount {}",
            operationCashDTO.getCard().getId(), operationCashDTO.getSum());
    operationService.withdrawMoney(operationCashDTO.getCard().getId(), operationCashDTO.getSum());
    return "redirect:/operation/result?type=withdraw&status=ok";
  }

  @GetMapping("/transfer")
  public String getTransfer(@AuthenticationPrincipal User user, Model model) {
    List<Card> enabledCards = cardService.getAllEnabledByUser(user);
    logger.info("getTransfer page with enabledCards {} for user {}", enabledCards,
        user.getUsername());
    model.addAttribute("operationTransferDTO", new OperationTransferDTO());
    model.addAttribute("cards", enabledCards);
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

    int senderCardId = cardService.getEnabledByCardNumb(operationTransferDTO.getCardNumb()).getId();
    logger.info("create transferMoney operation from cardId {} to cardNumb {} in amount {}",
        senderCardId, operationTransferDTO.getRecipientCardNumb(), operationTransferDTO.getSum());
    operationService.transferMoney(
        senderCardId,
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
    logger.info("getHistory page with enabledCards {} for user {}", cards,
        user.getUsername());
    model.addAttribute("cards", cards);
    model.addAttribute("directionSelected", directionId);
    model.addAttribute("typeSelected", typeId);

    if (cardId == null) {
      cardId = !cards.isEmpty() ? cards.get(0).getId() : null;
    }
    model.addAttribute("cardSelected", cardId);

    if (cardId != null) {
      logger.info("filtering operation for cardId {}, recipientCardNumber {}, directionId {},"
              + " typeId {}, startDate {}, endDate {}",
          cardId, recipientCardNumber, directionId, typeId, startDate, endDate);
      model.addAttribute("operations",
          OperationUtil.getListOperationHistoryTo(
              operationService
                  .getByFilter(cardId, recipientCardNumber, directionId, typeId, startDate,
                      endDate)));
    }
    return "/operations/history";
  }
}
