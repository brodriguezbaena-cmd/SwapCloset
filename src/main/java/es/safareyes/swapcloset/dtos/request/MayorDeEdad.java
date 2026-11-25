package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** La fecha de nacimiento debe corresponder a una persona con al menos 'minimo' años (RN-01). */
@Documented
@Constraint(validatedBy = MayorDeEdadValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MayorDeEdad {

    String message() default "Debes tener al menos {minimo} años para registrarte";

    int minimo() default 16;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
