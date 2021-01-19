package solvery.cards.controller.converter;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.format.Formatter;
import solvery.cards.util.DateTimeUtil;

public class DateTimeFormatters {

  public static class LocalDateFormatter implements Formatter<LocalDate> {

    @Override
    public LocalDate parse(String s, Locale locale) throws ParseException {
      return DateTimeUtil.parseLocalDate(s);
    }

    @Override
    public String print(LocalDate localDate, Locale locale) {
      return localDate.format(java.time.format.DateTimeFormatter.ISO_DATE);
    }
  }
}
