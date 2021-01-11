package solvery.cards.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Operations")
public class Operation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "card_id")
  private Card card;

  private String recipientCardNumb;

  private Integer sum;

  private Integer cardBalance;

  private LocalDateTime dateTime;

  public static Operation empty() {
    return new Operation(null, "", 0, 0, LocalDateTime.MIN);
  }

  public Operation() {
  }


  public Operation(Long id, Card card, String recipientCardNumb, Integer sum, Integer cardBalance, LocalDateTime dateTime) {
    this.id = id;
    this.card = card;
    this.recipientCardNumb = recipientCardNumb;
    this.sum = sum;
    this.cardBalance = cardBalance;
    this.dateTime = dateTime;
  }

  public Operation(Card card, String recipientCardNumb, Integer sum, Integer cardBalance, LocalDateTime dateTime) {
    this(null, card, recipientCardNumb, sum, cardBalance, dateTime);
  }

  public Operation(Card card, String recipientCardNumb, Integer sum, LocalDateTime dateTime) {
    this(null, card, recipientCardNumb, sum, null, dateTime);
  }

  public Operation(Card card, Integer sum, LocalDateTime dateTime) {
    this(null, card, null, sum, null, dateTime);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public String getRecipientCardNumb() {
    return recipientCardNumb;
  }

  public void setRecipientCardNumb(String recipientCardNumb) {
    this.recipientCardNumb = recipientCardNumb;
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
