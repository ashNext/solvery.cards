package solvery.cards.dto;

import org.hibernate.validator.constraints.Range;
import solvery.cards.model.Card;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OperationTransferTo extends OperationAddTo {

  @NotBlank
  @Size(min = 2, max = 16)
  private String recipientCardNumb;

  public OperationTransferTo() {
  }

  public OperationTransferTo(
      Integer id,
      @NotNull Card card,
      @NotNull @Range(min = 1, max = 999999999) Integer sum,
      @NotBlank @Size(min = 2, max = 16) String recipientCardNumb) {
    super(id, card, sum);
    this.recipientCardNumb = recipientCardNumb;
  }

  public String getRecipientCardNumb() {
    return recipientCardNumb;
  }

  public void setRecipientCardNumb(String recipientCardNumb) {
    this.recipientCardNumb = recipientCardNumb;
  }
}
