package solvery.cards.validator.card;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueCardValidator extends AbstractCardValidator implements
    ConstraintValidator<UniqueCardNumber, String> {

  @Override
  public boolean isValid(String number, ConstraintValidatorContext constraintValidatorContext) {
    return number != null && repository.findByNumb(number).isEmpty();
  }

  @Override
  public void initialize(UniqueCardNumber constraintAnnotation) {

  }
}
