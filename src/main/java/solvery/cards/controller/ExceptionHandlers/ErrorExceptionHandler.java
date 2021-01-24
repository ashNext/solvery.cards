package solvery.cards.controller.ExceptionHandlers;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import solvery.cards.util.ValidationUtil;
import solvery.cards.util.exception.BalanceOutRangeException;
import solvery.cards.util.exception.NotFoundException;

@ControllerAdvice
public class ErrorExceptionHandler {

  private static final String DEFAULT_ERROR_VIEW = "error";

  public static final String EXCEPTION_DUPLICATE_CARD = "card.uniqueCardNumber";
  public static final String EXCEPTION_DUPLICATE_USERNAME = "user.userNameExist";
  public static final String EXCEPTION_DUPLICATE_EMAIL = "user.emailExist";

  private static final Map<String, String> MAP_DUPLICATE_EXCEPTION = Map.of(
      "cards_unique_numb_idx", EXCEPTION_DUPLICATE_CARD,
      "users_unique_username_idx", EXCEPTION_DUPLICATE_USERNAME,
      "users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL);

  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(value = DataIntegrityViolationException.class)
  public ModelAndView conflictDuplicate(DataIntegrityViolationException e) {
    Throwable rootCause = ValidationUtil.getRootCause(e);

    String message = ValidationUtil.getMessage(rootCause);

    if (rootCause.getMessage() != null) {
      String lowerCaseMsg = rootCause.getMessage().toLowerCase();
      for (Map.Entry<String, String> entry : MAP_DUPLICATE_EXCEPTION.entrySet()) {
        if (lowerCaseMsg.contains(entry.getKey())) {
          message = entry.getValue();
          break;
        }
      }
    }

    return getModelAndViewError(message, HttpStatus.CONFLICT);
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(value = NotFoundException.class)
  public ModelAndView notFound(NotFoundException e) {
    Throwable rootCause = ValidationUtil.getRootCause(e);
    String message = ValidationUtil.getMessage(rootCause);

    return getModelAndViewError(message, HttpStatus.NOT_FOUND);
  }

  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(value = BalanceOutRangeException.class)
  public ModelAndView balanceOutRange(BalanceOutRangeException e) {
    Throwable rootCause = ValidationUtil.getRootCause(e);
    String message = ValidationUtil.getMessage(rootCause);

    return getModelAndViewError(message, HttpStatus.CONFLICT);
  }

  private ModelAndView getModelAndViewError(String message, HttpStatus httpStatus) {
    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.setStatus(httpStatus);
    mav.addObject("message", message);
    return mav;
  }
}
