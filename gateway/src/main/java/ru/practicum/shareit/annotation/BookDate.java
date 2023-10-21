package ru.practicum.shareit.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BookDateConstraintValidator.class)
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BookDate {
    String message() default "{Book and date have to be after start date}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
