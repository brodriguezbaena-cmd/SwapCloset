package es.safareyes.swapcloset.dtos.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class MayorDeEdadValidator implements ConstraintValidator<MayorDeEdad, LocalDate> {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");

    private int minimo;

    @Override
    public void initialize(MayorDeEdad anotacion) {
        this.minimo = anotacion.minimo();
    }

    @Override
    public boolean isValid(LocalDate nacimiento, ConstraintValidatorContext contexto) {
        if (nacimiento == null) {
            return true; // la obligatoriedad la comprueba @NotNull
        }
        return Period.between(nacimiento, LocalDate.now(MADRID)).getYears() >= minimo;
    }
}
