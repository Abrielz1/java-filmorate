package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {

    private int id;
    @NotNull
    @NotBlank
    private final String name;
    @Size(max = 200, message = "слишком длинное описание, юольше 200 символов")
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    private final long duration;

}
