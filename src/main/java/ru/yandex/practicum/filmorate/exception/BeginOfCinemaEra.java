package ru.yandex.practicum.filmorate.exception;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BeginOfCinemaEraValidator.class)
public @interface BeginOfCinemaEra {
    String message() default "Создание фильма прервано! Дата релиза раньше появления первого фильма.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
