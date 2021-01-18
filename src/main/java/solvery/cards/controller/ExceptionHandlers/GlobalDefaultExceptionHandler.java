package solvery.cards.controller.ExceptionHandlers;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import solvery.cards.util.ValidationUtil;
import solvery.cards.util.exception.ApplicationException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
  private static final String DEFAULT_ERROR_VIEW = "exception";

  @ExceptionHandler(ApplicationException.class)
  public ModelAndView applicationErrorHandler(HttpServletRequest req, ApplicationException appEx) throws Exception {
    return getModelAndViewException(req, appEx);
  }

  @ExceptionHandler(value = Exception.class)
  public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
      throw e;
    }

    return getModelAndViewException(req, e);
  }

  private ModelAndView getModelAndViewException(HttpServletRequest req, Exception e) {
    Throwable rootCause = ValidationUtil.getRootCause(e);

    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    mav.addObject("status", mav.getStatus());
    mav.addObject("url", req.getRequestURL());
    mav.addObject("message", ValidationUtil.getMessage(rootCause));
    mav.addObject("exception", rootCause);
    return mav;
  }
}
