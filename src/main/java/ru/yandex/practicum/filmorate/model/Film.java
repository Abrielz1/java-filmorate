package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class Film {
    @NonNull
    int id;
    @NonNull
    @NotBlank
    String name;
    @NonNull
    String description;
    @NonNull
    LocalDate releaseDate;
    @NonNull
    long duration;
}
