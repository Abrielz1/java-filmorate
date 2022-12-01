package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.validate.BeginOfCinemaEra;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {


    @PositiveOrZero
    private int id;
    @NotBlank(message = "Не правильное название фильма")
    private String name;
    @NotNull(message = "Отсутствует описание фильма")
    @Size(max = 200, message = "слишком длинное описание, больше 200 символов")
    private String description;
    @NotNull
    @BeginOfCinemaEra
    private LocalDate releaseDate;
    @Min(value = 1, message = "Неправильная продолжительность фильма")
    @Positive
    private long duration;
    private Mpa mpa;
    private List<Genre> genres;
}
