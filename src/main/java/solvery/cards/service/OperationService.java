package solvery.cards.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.Operation;
import solvery.cards.repository.OperationRepository;

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
    operation.setCardBalance(getLastBalance(operation.getCardNumb()) + operation.getSum());
    return repository.save(operation);
  }

  private Integer getLastBalance(String cardNumb) {
    Operation operation = getLastOperationOrEmpty(cardNumb);
    return operation.getCardBalance();
  }

  private Operation getLastOperationOrEmpty(String cardNumb) {
    return repository.findFirstByCardNumbOrderByDateTimeDesc(cardNumb).orElse(Operation.empty());
  }

  public void addMoney(String cardNumber, Integer sum) {
    create(new Operation(cardNumber, sum, LocalDateTime.now()));
  }

  public void refreshBalance(String cardNumber) {
    cardService.refreshBalanceByCardNumb(cardNumber, getLastBalance(cardNumber));
  }

  @Transactional
  public void transferMoney(String senderCardNumb, String recipientCardNumb, Integer sum) {
    create(new Operation(senderCardNumb, -sum, LocalDateTime.now()));
    create(new Operation(recipientCardNumb, sum, LocalDateTime.now()));
  }

  public List<Operation> getByFilter(String cardNumb, LocalDateTime startDate, LocalDateTime endDate) {
    return repository.getByFilter(cardNumb, startDate, endDate);
  }
}
