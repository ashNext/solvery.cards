package solvery.cards.validator.user;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler;
import solvery.cards.dto.UserRegistrationTo;
import solvery.cards.model.User;

@Component
public class UniqueUserUsernameValidator extends AbstractUserValidator {

  @Override
  public void validate(Object o, Errors errors) {
    UserRegistrationTo userTo = (UserRegistrationTo) o;
    User user = repository.findByUsername(userTo.getUsername().toLowerCase());

    if (user != null) {
      errors.rejectValue("username", user.getUsername(), null, messageSourceAccessor.getMessage(
          ErrorExceptionHandler.EXCEPTION_DUPLICATE_USERNAME));
    }
  }
}
