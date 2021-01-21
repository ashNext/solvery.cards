package solvery.cards.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.Card;
import solvery.cards.model.Operation;
import solvery.cards.repository.OperationRepository;
import solvery.cards.util.CardUtil;
import solvery.cards.util.DateTimeUtil;
import solvery.cards.util.exception.BalanceOutRangeException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationService {

  private final OperationRepository repository;

  private final CardService cardService;

  public OperationService(OperationRepository repository, CardService cardService) {
    this.repository = repository;
    this.cardService = cardService;
  }

  public Operation create(Operation operation) {
    checkAbilityChangeAndApplyBalance(operation);
    return repository.save(operation);
  }

  public void addMoney(Integer cardId, Integer sum) {
    create(new Operation(cardService.getEnabledById(cardId), sum, LocalDateTime.now()));
  }

  @Transactional
  public void transferMoney(Integer cardId, String recipientCardNumb, Integer sum) {
    Card card = cardService.getEnabledById(cardId);
    Card recipientCard = cardService.getEnabledByCardNumb(recipientCardNumb);
    create(new Operation(card, recipientCardNumb, -sum, LocalDateTime.now()));
    create(new Operation(recipientCard, card.getNumb(), sum, LocalDateTime.now()));
  }

  public List<Operation> getByFilter(Integer cardId, String recipientCardNumb, LocalDate startDate, LocalDate endDate) {
    Card card = cardService.getEnabledById(cardId);
    LocalDateTime start = DateTimeUtil.startOrMinDate(startDate);
    LocalDateTime end = DateTimeUtil.endOrMaxDate(endDate);
    return recipientCardNumb == null || recipientCardNumb.isBlank() ?
        repository.getByFilter(card, start, end) :
        repository.getByFilterWithRecipientCardNumb(card, recipientCardNumb, start, end);
  }

  public List<Operation> getLastNDays(Integer cardId, int days) {
    Card card = cardService.getEnabledById(cardId);
    LocalDateTime start = DateTimeUtil.startOrMinDate(LocalDate.now().minusDays(days));
    LocalDateTime end = LocalDateTime.now();
    return repository.getByFilter(card, start, end);
  }

  public List<Operation> getLast30Days(Integer cardId) {
    return getLastNDays(cardId, 30);
  }

  private void checkAbilityChangeAndApplyBalance(Operation operation) {
    Integer newBalance = operation.getCard().getBalance() + operation.getSum();

    if (newBalance > CardUtil.MAX_BALANCE) {
      throw new BalanceOutRangeException("Невозможно зачислить средства на карту \""
          + operation.getCard().getNumb() + "\", т.к. будет первышен лимит баланса");
    }

    if (newBalance < CardUtil.MIN_BALANCE) {
      throw new BalanceOutRangeException("На карте \""
          + operation.getCard().getNumb() + "\" не хватает средств для совершения операции");
    }

    operation.getCard().setBalance(newBalance);
    operation.setCardBalance(newBalance);
  }
}
