package solvery.cards.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class DateTimeUtil {

  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

  private static final LocalDateTime MIN_DATE = LocalDateTime.of(1, 1, 1, 0, 0);
  private static final LocalDateTime MAX_DATE = LocalDateTime.of(3000, 1, 1, 0, 0);

  private DateTimeUtil() {
  }

  public static LocalDateTime startOrMinDate(LocalDate localDate) {
    return localDate != null ? localDate.atStartOfDay() : MIN_DATE;
  }

  public static LocalDateTime endOrMaxDate(LocalDate localDate) {
    return localDate != null ? localDate.plusDays(1).atStartOfDay().minus(1, ChronoUnit.NANOS) : MAX_DATE;
  }

  public static @Nullable
  LocalDate parseLocalDate(@Nullable String value) {
    return StringUtils.isEmpty(value) ? null : LocalDate.parse(value);
  }
}
