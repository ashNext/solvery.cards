package solvery.cards.validator.user;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler;
import solvery.cards.dto.UserRegistrationDTO;
import solvery.cards.model.User;

@Component
public class UniqueUserUsernameValidator extends AbstractUserValidator {

  @Override
  @Transactional
  public void validate(Object o, Errors errors) {
    UserRegistrationDTO userTo = (UserRegistrationDTO) o;
    User user = repository.findByUsername(userTo.getUsername().toLowerCase());

    if (user != null) {
      errors.rejectValue("username", user.getUsername(), null, messageSourceAccessor.getMessage(
          ErrorExceptionHandler.EXCEPTION_DUPLICATE_USERNAME, LocaleContextHolder.getLocale()));
    }
  }
}
