package ru.yandex.practicum.filmorate.exception;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BeginOfCinemaEraValidator implements ConstraintValidator<BeginOfCinemaEra, LocalDate> {

    private static final LocalDate BEGIN_OF_CINEMA_ERA = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return !date.isBefore(BEGIN_OF_CINEMA_ERA);
    }
}
