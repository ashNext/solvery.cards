package solvery.cards.service;

import static solvery.cards.util.specification.OperationSpecification.fromDate;
import static solvery.cards.util.specification.OperationSpecification.toDate;
import static solvery.cards.util.specification.OperationSpecification.withCard;
import static solvery.cards.util.specification.OperationSpecification.withDirection;
import static solvery.cards.util.specification.OperationSpecification.withRecipient;
import static solvery.cards.util.specification.OperationSpecification.withType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import solvery.cards.model.Card;
import solvery.cards.model.Operation;
import solvery.cards.repository.OperationRepository;
import solvery.cards.util.CardUtil;
import solvery.cards.util.exception.BalanceOutRangeException;
import solvery.cards.util.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class OperationService {

  private final OperationRepository repository;

  private final CardService cardService;

  public OperationService(OperationRepository repository, CardService cardService) {
    this.repository = repository;
    this.cardService = cardService;
  }

  public Operation get(long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NotFoundException("operation.notFound"));
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public Operation create(Operation operation) {
    checkAbilityChangeAndApplyBalance(operation);
    return repository.save(operation);
  }

  private Operation moveMoneyByCardId(Integer cardId, String recipientCardNumb, Integer sum) {
    return moveMoney(cardService.getEnabledById(cardId), recipientCardNumb, sum);
  }

  private Operation moveMoney(Card card, String recipientCardNumb, Integer sum) {
    return create(new Operation(card, recipientCardNumb, sum, LocalDateTime.now()));
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public Operation addMoney(Integer cardId, Integer sum) {
    return moveMoneyByCardId(cardId, null, sum);
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public Operation withdrawMoney(Integer cardId, Integer sum) {
    return moveMoneyByCardId(cardId, null, -sum);
  }

  @Transactional
  @CacheEvict(value = {"cards", "cardsEnabled"}, allEntries = true)
  public void transferMoney(Integer cardId, String recipientCardNumb, Integer sum) {
    Card card = cardService.getEnabledById(cardId);
    Card recipientCard = cardService.getEnabledByCardNumb(recipientCardNumb);
    moveMoney(card, recipientCardNumb, -sum);
    moveMoney(recipientCard, card.getNumb(), sum);
  }

  public List<Operation> getByFilter(Integer cardId, String recipientCardNumb, int directionId,
      int typeId, LocalDate startDate, LocalDate endDate) {
    Card card = cardService.getById(cardId);

//    if (!StringUtils.hasText(recipientCardNumb) && startDate == null && endDate == null
//        && directionId == 0 && typeId == 0) {
//      startDate = LocalDate.now().minusDays(2);
//      endDate = LocalDate.now();
//    }

    if (StringUtils.hasText(recipientCardNumb) && typeId == 1) {
      recipientCardNumb = null;
    }

    return repository.findAll(
        Specification.where(withCard(card))
            .and(withRecipient(recipientCardNumb))
            .and(withDirection(directionId))
            .and(withType(typeId))
            .and(fromDate(startDate))
            .and(toDate(endDate)),
        Sort.by(Sort.Direction.DESC, "dateTime"));
  }

  private void checkAbilityChangeAndApplyBalance(Operation operation) {
    int newBalance = operation.getCard().getBalance() + operation.getSum();

    if (newBalance > CardUtil.MAX_BALANCE) {
      throw new BalanceOutRangeException("operation.over.balance", operation.getCard().getNumb());
    }

    if (newBalance < CardUtil.MIN_BALANCE) {
      throw new BalanceOutRangeException("operation.lower.balance", operation.getCard().getNumb());
    }

//    operation.getCard().setBalance(newBalance);
    Card card = operation.getCard();
    card.setBalance(newBalance);
    cardService.update(card);
    operation.setCardBalance(newBalance);
  }
}
