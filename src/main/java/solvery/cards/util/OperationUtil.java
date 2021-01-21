package solvery.cards.util;

import org.springframework.util.StringUtils;
import solvery.cards.dto.OperationHistoryTo;
import solvery.cards.model.Operation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OperationUtil {

  private OperationUtil() {
  }

  public static List<OperationHistoryTo> getListOperationHistoryTo(Collection<Operation> operations) {
    return operations.stream()
        .map(OperationUtil::newTo)
        .collect(Collectors.toList());
  }

  private static OperationHistoryTo newTo(Operation o) {
    final boolean cashReceipts = o.getSum() > 0;

    String purposeOfPayment = o.getRecipientCardNumb();

    if (cashReceipts) {
      purposeOfPayment = StringUtils.isEmpty(purposeOfPayment) ? "Внесение наличных" : "Поступление с карты \"" + purposeOfPayment + "\"";
    } else {
      purposeOfPayment = StringUtils.isEmpty(purposeOfPayment) ? "Снятие наличных" : "Перевод на карту \"" + purposeOfPayment + "\"";
    }

    return new OperationHistoryTo(
        o.getId(), purposeOfPayment, Math.abs(o.getSum()), o.getCardBalance(), o.getDateTime(), cashReceipts);
  }
}
