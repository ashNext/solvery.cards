package solvery.cards.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import solvery.cards.validator.FieldsValueMatch;
import solvery.cards.validator.card.CheckCardAvailability;

@FieldsValueMatch.List({
    @FieldsValueMatch(
        field = "recipientCardNumb",
        fieldMatch = "cardNumb",
        mustMatch = false,
        message = "Номер карты получателя не может совпадать с номером карты отправителя!"
    )
})
public class OperationTransferTo {

  private Integer id;

  @NotBlank
  @Size(min = 2, max = 16)
  @CheckCardAvailability(message = "Карта отправителя не найдена")
  private String cardNumb;

  @NotNull
  @Range(min = 1, max = 999999999)
  private Integer sum;

  @NotBlank
  @Size(min = 2, max = 16)
  @CheckCardAvailability(message = "Карта получателя не найдена")
  private String recipientCardNumb;

  public OperationTransferTo() {
  }

  public OperationTransferTo(
      Integer id,
      @NotBlank @Size(min = 2, max = 16) String cardNumb,
      @NotNull @Range(min = 1, max = 999999999) Integer sum,
      @NotBlank @Size(min = 2, max = 16) String recipientCardNumb) {
    this.id = id;
    this.cardNumb = cardNumb;
    this.sum = sum;
    this.recipientCardNumb = recipientCardNumb;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCardNumb() {
    return cardNumb;
  }

  public void setCardNumb(String cardNumb) {
    this.cardNumb = cardNumb;
  }

  public Integer getSum() {
    return sum;
  }

  public void setSum(Integer sum) {
    this.sum = sum;
  }

  public String getRecipientCardNumb() {
    return recipientCardNumb;
  }

  public void setRecipientCardNumb(String recipientCardNumb) {
    this.recipientCardNumb = recipientCardNumb;
  }
}
