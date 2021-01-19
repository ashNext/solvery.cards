package solvery.cards.validator.card;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckCardValidator extends AbstractCardValidator implements
    ConstraintValidator<CheckCardAvailability, String> {

  @Override
  public boolean isValid(String number, ConstraintValidatorContext constraintValidatorContext) {
    return repository.findByNumb(number).isPresent();
  }

  @Override
  public void initialize(CheckCardAvailability constraintAnnotation) {

  }
}
