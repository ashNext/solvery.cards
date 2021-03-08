package solvery.cards.controller.ExceptionHandlers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import solvery.cards.util.exception.ApplicationException;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {

  private static final String DEFAULT_ERROR_VIEW = "exception";

  private final MessageSourceAccessor messageSourceAccessor;

  public GlobalDefaultExceptionHandler(
      MessageSourceAccessor messageSourceAccessor) {
    this.messageSourceAccessor = messageSourceAccessor;
  }

  @ExceptionHandler(ApplicationException.class)
  public ModelAndView applicationErrorHandler(HttpServletRequest req, ApplicationException appEx)
      throws Exception {
    return getModelAndViewException(req, appEx,
        messageSourceAccessor
            .getMessage(appEx.getMsgCode(), appEx.getArgs(), LocaleContextHolder.getLocale()));
  }

  @ExceptionHandler(value = Exception.class)
  public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
      throw e;
    }

    return getModelAndViewException(req, e, null);
  }

  private ModelAndView getModelAndViewException(HttpServletRequest req, Exception e, String msg) {
    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    mav.addObject("status", mav.getStatus());
    mav.addObject("url", req.getRequestURL());
    mav.addObject("message", msg);
    mav.addObject("exception", e);
    return mav;
  }
}
