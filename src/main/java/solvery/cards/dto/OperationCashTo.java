package solvery.cards.dto;

import org.hibernate.validator.constraints.Range;
import solvery.cards.model.Card;

import javax.validation.constraints.NotNull;

public class OperationCashTo {

  private Integer id;

  @NotNull
  private Card card;

  @NotNull
  @Range(min = 1, max = 999999999)
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
