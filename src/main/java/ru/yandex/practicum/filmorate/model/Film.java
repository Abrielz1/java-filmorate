package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class Film {

    int id;
    String name;
    String description;
    LocalDate releaseDate;
    long duration;
}
