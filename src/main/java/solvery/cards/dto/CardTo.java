package solvery.cards.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import solvery.cards.util.CardUtil;
import solvery.cards.validator.card.UniqueCardNumber;

public class CardTo {

  private Integer id;

  @NotBlank
  @Size(min = CardUtil.MIX_LENGTH_NUMB, max = CardUtil.MAX_LENGTH_NUMB)
  @UniqueCardNumber(message = "Уже зарегистрирована карта с таким номером!")
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
}
