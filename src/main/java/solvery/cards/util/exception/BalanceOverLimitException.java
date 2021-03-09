package solvery.cards.util.exception;

public class BalanceOverLimitException extends BalanceOutRangeException {

  public static final String CODE_BALANCE_OVER_LIMIT = "operation.over.balance";

  public BalanceOverLimitException(String arg) {
    super(CODE_BALANCE_OVER_LIMIT, arg);
  }
}
