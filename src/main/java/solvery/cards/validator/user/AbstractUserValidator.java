package solvery.cards.validator.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import solvery.cards.dto.UserRegistrationDTO;
import solvery.cards.repository.UserRepository;

public abstract class AbstractUserValidator implements Validator {

  @Autowired
  protected UserRepository repository;

  @Autowired
  protected MessageSourceAccessor messageSourceAccessor;

  @Override
  public boolean supports(Class<?> aClass) {
    return UserRegistrationDTO.class.isAssignableFrom(aClass);
  }

  @Override
  public abstract void validate(Object o, Errors errors);
}
