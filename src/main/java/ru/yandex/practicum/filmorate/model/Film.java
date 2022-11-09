package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.BeginOfCinemaEra;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Set<Integer> usersLikes = new HashSet<>();

    @PositiveOrZero
    private int id;
    @NotBlank(message = "Не правильное название фильма")
    private final String name;
    @NotNull(message = "Отсутствует описание фильма")
    @Size(max = 200, message = "слишком длинное описание, больше 200 символов")
    private final String description;
    @NotNull
    @BeginOfCinemaEra
    private final LocalDate releaseDate;
    @Min(value = 1, message = "Неправильная продолжительность фильма")
    @Positive
    private final long duration;

}
