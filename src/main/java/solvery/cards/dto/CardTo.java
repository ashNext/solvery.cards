package solvery.cards.dto;

import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import solvery.cards.util.CardUtil;
import solvery.cards.validator.card.UniqueCardNumber;

public class CardTo {

  private Integer id;

  @NotBlank(message = "{common.notBlank}")
  @Size(min = CardUtil.MIX_LENGTH_NUMB, max = CardUtil.MAX_LENGTH_NUMB, message = "{card.numbSize}")
  @UniqueCardNumber(message = "{card.uniqueCardNumber}")
  private String numb;

  private Integer balance;

  public CardTo() {
  }

  public CardTo(Integer id, String numb, Integer balance) {
    this.id = id;
    this.numb = numb;
    this.balance = balance;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNumb() {
    return numb;
  }

  public void setNumb(String numb) {
    this.numb = numb;
  }

  public Integer getBalance() {
    return balance;
  }

  public void setBalance(Integer balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardTo cardTo = (CardTo) o;
    return Objects.equals(id, cardTo.id) &&
        Objects.equals(numb, cardTo.numb) &&
        Objects.equals(balance, cardTo.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, numb, balance);
  }

  @Override
  public String toString() {
    return "CardTo{" +
        "id=" + id +
        ", numb='" + numb + '\'' +
        ", balance=" + balance +
        '}';
  }
}
