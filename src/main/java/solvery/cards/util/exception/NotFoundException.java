package solvery.cards.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends ApplicationException {

  public NotFoundException(String messageCode) {
    super(messageCode);
  }

  public NotFoundException(String messageCode, String arg) {
    super(messageCode, arg);
  }
}
