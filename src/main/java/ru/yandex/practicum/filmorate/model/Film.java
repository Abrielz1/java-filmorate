package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.validate.BeginOfCinemaEra;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
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

