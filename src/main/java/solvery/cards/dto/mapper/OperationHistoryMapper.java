package solvery.cards.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;
import solvery.cards.dto.OperationHistoryDTO;
import solvery.cards.model.Operation;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring")
public abstract class OperationHistoryMapper {

  @Autowired
  private MessageSourceAccessor messageSourceAccessor;

  public OperationHistoryDTO toOperationHistoryDTO(Operation operation) {
    final boolean isCashReceipts = operation.getSum() > 0;
    final Locale locale = LocaleContextHolder.getLocale();

    String purposeOfPayment = operation.getRecipientCardNumb();

    if (isCashReceipts) {
      purposeOfPayment =
          StringUtils.hasText(purposeOfPayment) ?
              messageSourceAccessor.getMessage(
                  "history.receipt.card", new String[]{purposeOfPayment}, locale) :
              messageSourceAccessor.getMessage("history.receipt.cash", locale);
    } else {
      purposeOfPayment =
          StringUtils.hasText(purposeOfPayment) ?
              messageSourceAccessor.getMessage(
                  "history.withdraw.card", new String[]{purposeOfPayment}, locale) :
              messageSourceAccessor.getMessage("history.withdraw.cash", locale);
    }

    return new OperationHistoryDTO(
        operation.getId(), purposeOfPayment, Math.abs(operation.getSum()),
        operation.getCardBalance(), operation.getDateTime(), isCashReceipts);
  }

  public abstract List<OperationHistoryDTO> toOperationHistoryDTO(Collection<Operation> operations);
}
