package solvery.cards.util;

public class ValidationUtil {

  private ValidationUtil() {
  }

  //  http://stackoverflow.com/a/28565320/548473
  public static Throwable getRootCause(Throwable t) {
    Throwable result = t;
    Throwable cause;

    while (null != (cause = result.getCause()) && (result != cause)) {
      result = cause;
    }
    return result;
  }

  public static String getMessage(Throwable e) {
    return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
  }
}
