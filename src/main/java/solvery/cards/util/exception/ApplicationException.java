package solvery.cards.util.exception;

import java.util.Arrays;

public class ApplicationException extends RuntimeException {

  private final String msgCode;
  private final String[] args;

  public ApplicationException(String msgCode, String... args) {
    super(String.format("msgCode=%s, args=%s", msgCode, Arrays.toString(args)));
    this.msgCode = msgCode;
    this.args = args;
  }

  public String getMsgCode() {
    return msgCode;
  }

  public String[] getArgs() {
    return args;
  }
}
