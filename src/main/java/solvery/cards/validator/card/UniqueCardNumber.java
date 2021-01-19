package solvery.cards.validator.card;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UniqueCardValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCardNumber {

  String message() default "Duplicate card number";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
