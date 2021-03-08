package solvery.cards.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class BalanceOutRangeException extends ApplicationException {

  public BalanceOutRangeException(String messageCode, String arg) {
    super(messageCode, arg);
  }
}
