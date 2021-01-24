package solvery.cards.dto;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import solvery.cards.model.Card;

public class OperationCashTo {

  private Integer id;

  @NotNull(message = "{common.notBlank}")
  private Card card;

  @NotNull(message = "{common.notBlank}")
  @Range(min = 1, max = 999999999, message = "{operation.sumRange}")
  private Integer sum;

  public OperationCashTo() {
  }

  public OperationCashTo(Integer id, @NotNull Card card, @NotNull @Range(min = 1, max = 999999999) Integer sum) {
    this.id = id;
    this.card = card;
    this.sum = sum;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public Integer getSum() {
    return sum;
  }

  public void setSum(Integer sum) {
    this.sum = sum;
  }
}
