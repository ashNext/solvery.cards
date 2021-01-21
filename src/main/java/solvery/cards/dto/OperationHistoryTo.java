package solvery.cards.dto;

import java.time.LocalDateTime;

public class OperationHistoryTo {

  private final Long id;

  private final String purposeOfPayment;

  private final Integer sum;

  private final Integer cardBalance;

  private final LocalDateTime dateTime;

  private final boolean cashReceipts;

  public OperationHistoryTo(Long id, String purposeOfPayment, Integer sum, Integer cardBalance, LocalDateTime dateTime, boolean cashReceipts) {
    this.id = id;
    this.purposeOfPayment = purposeOfPayment;
    this.sum = sum;
    this.cardBalance = cardBalance;
    this.dateTime = dateTime;
    this.cashReceipts = cashReceipts;
  }

  public Long getId() {
    return id;
  }

  public String getPurposeOfPayment() {
    return purposeOfPayment;
  }

  public Integer getSum() {
    return sum;
  }

  public Integer getCardBalance() {
    return cardBalance;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public boolean isCashReceipts() {
    return cashReceipts;
  }
}
