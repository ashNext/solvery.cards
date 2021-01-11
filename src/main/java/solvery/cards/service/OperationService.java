package solvery.cards.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvery.cards.model.Card;
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
    Integer newBalance = operation.getCard().getBalance() + operation.getSum();
    operation.getCard().setBalance(newBalance);
    operation.setCardBalance(newBalance);
    return repository.save(operation);
  }

  public void addMoney(Integer cardId, Integer sum) {
    create(new Operation(cardService.getById(cardId), sum, LocalDateTime.now()));
  }

  @Transactional
  public void transferMoney(Integer cardId, String recipientCardNumb, Integer sum) {
    Card card = cardService.getById(cardId);
    Card recipientCard = cardService.getByCardNumb(recipientCardNumb);
    create(new Operation(card, recipientCardNumb, -sum, LocalDateTime.now()));
    create(new Operation(recipientCard, card.getNumb(), sum, LocalDateTime.now()));
  }

  public List<Operation> getByFilter(Integer cardId, String recipientCardNumb, LocalDateTime startDate, LocalDateTime endDate) {
    return recipientCardNumb == null ?
            repository.getByFilter(cardService.getById(cardId), startDate, endDate) :
            repository.getByFilterWithRecipientCardNumb(cardService.getById(cardId), recipientCardNumb, startDate, endDate);
  }
}
