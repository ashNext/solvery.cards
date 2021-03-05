package solvery.cards.validator.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import solvery.cards.controller.ExceptionHandlers.ErrorExceptionHandler;
import solvery.cards.dto.UserRegistrationDTO;
import solvery.cards.model.User;

@Component
public class UniqueUserMailValidator extends AbstractUserValidator {

  @Override
  @Transactional
  public void validate(Object o, Errors errors) {
    UserRegistrationDTO userTo = (UserRegistrationDTO) o;
    User user = repository.getByEmail(userTo.getEmail().toLowerCase());

    if (user != null) {
      errors.rejectValue("email", user.getEmail(), null, messageSourceAccessor.getMessage(
          ErrorExceptionHandler.EXCEPTION_DUPLICATE_EMAIL));
    }
  }
}
