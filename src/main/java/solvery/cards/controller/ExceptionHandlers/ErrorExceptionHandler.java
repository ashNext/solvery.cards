package solvery.cards.controller.ExceptionHandlers;

import java.util.Map;
import java.util.Objects;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import solvery.cards.util.exception.BalanceOutRangeException;
import solvery.cards.util.exception.NotFoundException;

@ControllerAdvice
public class ErrorExceptionHandler {

  private static final String DEFAULT_ERROR_VIEW = "error";

  public static final String EXCEPTION_DUPLICATE_CARD = "card.uniqueCardNumber";
  public static final String EXCEPTION_DUPLICATE_USERNAME = "user.userNameExist";
  public static final String EXCEPTION_DUPLICATE_EMAIL = "user.emailExist";
  public static final String EXCEPTION_NO_MATCH_RETYPE_PASSWORD = "user.matchRetypePassword";

  private static final Map<String, String> MAP_DUPLICATE_EXCEPTION = Map.of(
      "cards_unique_numb_idx", EXCEPTION_DUPLICATE_CARD,
      "users_unique_username_idx", EXCEPTION_DUPLICATE_USERNAME,
      "users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL);

  private final MessageSourceAccessor messageSourceAccessor;

  public ErrorExceptionHandler(
      MessageSourceAccessor messageSourceAccessor) {
    this.messageSourceAccessor = messageSourceAccessor;
  }

  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(value = DataIntegrityViolationException.class)
  public ModelAndView conflictDuplicate(DataIntegrityViolationException e) {
    String message = Objects.requireNonNull(e.getRootCause()).getMessage();

    if (message != null) {
      String lowerCaseMsg = message.toLowerCase();
      for (Map.Entry<String, String> entry : MAP_DUPLICATE_EXCEPTION.entrySet()) {
        if (lowerCaseMsg.contains(entry.getKey())) {
          message = messageSourceAccessor
              .getMessage(entry.getValue(), LocaleContextHolder.getLocale());
          break;
        }
      }
    }

    return getModelAndViewError(message, HttpStatus.CONFLICT);
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(value = NotFoundException.class)
  public ModelAndView notFound(NotFoundException e) {
    return getModelAndViewError(messageSourceAccessor
            .getMessage(e.getMsgCode(), e.getArgs(), LocaleContextHolder.getLocale()),
        HttpStatus.NOT_FOUND);
  }

  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(value = BalanceOutRangeException.class)
  public ModelAndView balanceOutRange(BalanceOutRangeException e) {
    return getModelAndViewError(messageSourceAccessor
            .getMessage(e.getMsgCode(), e.getArgs(), LocaleContextHolder.getLocale()),
        HttpStatus.CONFLICT);
  }

  private ModelAndView getModelAndViewError(String message, HttpStatus httpStatus) {
    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.setStatus(httpStatus);
    mav.addObject("message", message);
    return mav;
  }
}
