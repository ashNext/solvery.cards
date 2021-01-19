package solvery.cards.controller.ExceptionHandlers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import solvery.cards.util.ValidationUtil;
import solvery.cards.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class ErrorExceptionHandler {
  private static final String DEFAULT_ERROR_VIEW = "error";

  private static final String EXCEPTION_DUPLICATE_CARD = "Уже зарегистрирована карта с таким номером!";

  private static final Map<String, String> MAP_DUPLICATE_EXCEPTION = Map.of(
      "cards_unique_numb_idx", EXCEPTION_DUPLICATE_CARD);

  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(value = DataIntegrityViolationException.class)
  public ModelAndView conflict(HttpServletRequest req, DataIntegrityViolationException e) throws Exception {
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
  public ModelAndView conflict(HttpServletRequest req, NotFoundException e) throws Exception {
    Throwable rootCause = ValidationUtil.getRootCause(e);

    String message = ValidationUtil.getMessage(rootCause);

    return getModelAndViewError(message, HttpStatus.NOT_FOUND);
  }

  private ModelAndView getModelAndViewError(String message, HttpStatus httpStatus) {
    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.setStatus(httpStatus);
    mav.addObject("message", message);
    return mav;
  }
}
