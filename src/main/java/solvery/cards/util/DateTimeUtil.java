package solvery.cards.util;

import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

  private DateTimeUtil() {
  }
}
