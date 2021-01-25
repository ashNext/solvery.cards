package solvery.cards.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import solvery.cards.util.DateTimeUtil;

@Entity
@Table(name = "operations")
public class Operation {

  @Id
  @SequenceGenerator(name = "operations_seq_gen", sequenceName = "operations_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operations_seq_gen")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "card_id")
  @NotNull
  private Card card;

  @Column(name = "recipient_card_numb")
  @Size(max = 16)
  private String recipientCardNumb;

  @Column(name = "sum", nullable = false)
  @NotNull
  @Range(min = -999999999, max = 999999999)
  private Integer sum;

  @Column(name = "card_balance", nullable = false)
  @NotNull
  @Range(min = 0, max = 999999999)
  private Integer cardBalance;

  @Column(name = "date_time", nullable = false, columnDefinition = "timestamp default now()")
  @NotNull
  @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
  private LocalDateTime dateTime;

  public Operation() {
  }

  public Operation(Long id, Card card, String recipientCardNumb, Integer sum, Integer cardBalance,
                   LocalDateTime dateTime) {
    this.id = id;
    this.card = card;
    this.recipientCardNumb = recipientCardNumb;
    this.sum = sum;
    this.cardBalance = cardBalance;
    this.dateTime = dateTime;
  }

  public Operation(Card card, String recipientCardNumb, Integer sum, Integer cardBalance,
                   LocalDateTime dateTime) {
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
