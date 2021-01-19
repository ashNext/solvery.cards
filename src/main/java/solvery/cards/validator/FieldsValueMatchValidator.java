package solvery.cards.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldsValueMatch, Object> {

  private String field;
  private String fieldMatch;
  private boolean mustMatch;
  private boolean ignoreCase;

  @Override
  public void initialize(FieldsValueMatch constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.fieldMatch = constraintAnnotation.fieldMatch();
    this.mustMatch = constraintAnnotation.mustMatch();
    this.ignoreCase = constraintAnnotation.ignoreCase();
  }

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
    String fieldValue = (String) new BeanWrapperImpl(o).getPropertyValue(field);
    String fieldMatchValue = (String) new BeanWrapperImpl(o).getPropertyValue(fieldMatch);

    boolean isValid;

    if (fieldValue != null) {
      if (mustMatch) {
        isValid = (ignoreCase) ? fieldValue.equalsIgnoreCase(fieldMatchValue)
            : fieldValue.equals(fieldMatchValue);
      } else {
        isValid = (ignoreCase) ? !fieldValue.equalsIgnoreCase(fieldMatchValue)
            : !fieldValue.equals(fieldMatchValue);
      }
    } else {
      isValid = fieldMatchValue == null;
    }

    if (!isValid) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext.buildConstraintViolationWithTemplate(
          constraintValidatorContext.getDefaultConstraintMessageTemplate())
          .addPropertyNode(field).addConstraintViolation();
    }

    return isValid;
  }
}
