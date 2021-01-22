package solvery.cards.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

  private static final LocalDateTime MIN_DATE = LocalDateTime.of(1, 1, 1, 0, 0);
  private static final LocalDateTime MAX_DATE = LocalDateTime.of(3000, 1, 1, 0, 0);

  private DateTimeUtil() {
  }

  public static LocalDateTime atStartOfDay(@NotNull LocalDate localDate) {
    return localDate.atStartOfDay();
  }

  public static LocalDateTime atStartOfDayOrMinDate(@Nullable LocalDate localDate) {
    return localDate != null ? atStartOfDay(localDate) : MIN_DATE;
  }

  @Nullable
  public static LocalDateTime atStartOfDayOrNull(@Nullable LocalDate localDate) {
    return localDate != null ? atStartOfDay(localDate) : null;
  }

  public static LocalDateTime atEndOfDay(@NotNull LocalDate localDate) {
    return localDate.atStartOfDay().plusDays(1).minusNanos(1);
  }

  public static LocalDateTime atEndOfDayOrMaxDate(@Nullable LocalDate localDate) {
    return localDate != null ? atEndOfDay(localDate) : MAX_DATE;
  }

  @Nullable
  public static LocalDateTime atEndOfDayOrNull(@Nullable LocalDate localDate) {
    return localDate != null ? atEndOfDay(localDate) : null;
  }

  @Nullable
  public static LocalDate parseLocalDate(@Nullable String value) {
    return StringUtils.hasText(value) ? LocalDate.parse(value) : null;
  }
}
