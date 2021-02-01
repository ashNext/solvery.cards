package solvery.cards.validator.card;

import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckCardValidator extends AbstractCardValidator implements
    ConstraintValidator<CheckCardAvailability, String> {

  @Override
  @Transactional
  public boolean isValid(String number, ConstraintValidatorContext constraintValidatorContext) {
    return repository.findByNumbAndEnabledTrue(number).isPresent();
  }

  @Override
  public void initialize(CheckCardAvailability constraintAnnotation) {

  }
}
