package solvery.cards.controller.ExceptionHandlers;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import solvery.cards.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
  public static final String DEFAULT_ERROR_VIEW = "exception";

  @ExceptionHandler(value = Exception.class)
  public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    if (AnnotationUtils.findAnnotation
        (e.getClass(), ResponseStatus.class) != null)
      throw e;

    Throwable rootCause = ValidationUtil.getRootCause(e);

    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR);
    mav.addObject("url", req.getRequestURL());
    mav.addObject("message", ValidationUtil.getMessage(rootCause));
    mav.addObject("exception", rootCause);
    return mav;
  }
}
