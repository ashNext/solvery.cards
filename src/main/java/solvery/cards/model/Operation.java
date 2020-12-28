package solvery.cards.model;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Operations")
public class Operation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String cardNumb;

  private Integer sum;

  private Integer cardBalance;

  private LocalDateTime dateTime;

  public static Operation empty() {
    return new Operation("", 0, 0, LocalDateTime.MIN);
  }

  public Operation() {
  }

  public Operation(Long id, String cardNumb, Integer sum, Integer cardBalance,
      LocalDateTime dateTime) {
    this.id = id;
    this.cardNumb = cardNumb;
    this.sum = sum;
    this.cardBalance = cardBalance;
    this.dateTime = dateTime;
  }

  public Operation(String cardNumb, Integer sum, Integer cardBalance,
      LocalDateTime dateTime) {
    this(null, cardNumb, sum, cardBalance, dateTime);
  }

  public Operation(String cardNumb, Integer sum, LocalDateTime dateTime) {
    this(null, cardNumb, sum, null, dateTime);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  public Integer getCardBalance() {
    return cardBalance;
  }

  public void setCardBalance(Integer cardBalance) {
    this.cardBalance = cardBalance;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }
}
