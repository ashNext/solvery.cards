package solvery.cards.util.exception;

public class BalanceNegativeException extends BalanceOutRangeException {

  public static final String CODE_BALANCE_NEGATIVE = "operation.lower.balance";

  public BalanceNegativeException(String arg) {
    super(CODE_BALANCE_NEGATIVE, arg);
  }
}
