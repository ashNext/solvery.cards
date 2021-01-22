package solvery.cards.util.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import solvery.cards.model.Card;
import solvery.cards.model.Operation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static solvery.cards.util.DateTimeUtil.atEndOfDay;
import static solvery.cards.util.DateTimeUtil.atStartOfDay;

public class OperationSpecification {

  public static Specification<Operation> withCard(@NotNull final Card card) {
    return (root, cq, cb) -> cb.equal(root.get("card").get("id"), card.getId());
  }

  @Nullable
  public static Specification<Operation> withRecipient(@Nullable final String recipient) {
    return StringUtils.hasText(recipient) ? (root, cq, cb) -> cb.equal(root.get("recipientCardNumb"), recipient)
        : null;
  }

  @Nullable
  public static Specification<Operation> fromDate(@Nullable final LocalDate localDate) {
    return ObjectUtils.isEmpty(localDate) ? null
        : (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("dateTime"), atStartOfDay(localDate));
  }

  @Nullable
  public static Specification<Operation> toDate(@Nullable final LocalDate localDate) {
    return ObjectUtils.isEmpty(localDate) ? null
        : (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("dateTime"), atEndOfDay(localDate));
  }

  @Nullable
  public static Specification<Operation> withDirection(final int direction) {
    return direction == 0 ? null
        : (root, cq, cb) -> direction < 0 ? cb.lessThan(root.get("sum"), 0) : cb.greaterThan(root.get("sum"), 0);
  }

  @Nullable
  public static Specification<Operation> withType(final int type) {
    return (root, cq, cb) ->
        type == 1 ? cb.isNull(root.get("recipientCardNumb"))
            : type == 2 ? cb.isNotNull(root.get("recipientCardNumb"))
            : null;
  }
}
