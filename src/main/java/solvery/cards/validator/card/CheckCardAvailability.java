package solvery.cards.validator.card;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = CheckCardValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckCardAvailability {

  String message() default "Card is not found";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
