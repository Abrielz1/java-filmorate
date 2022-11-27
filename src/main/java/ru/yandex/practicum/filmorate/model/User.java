package ru.yandex.practicum.filmorate.model;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
public class User {

    private Set<Integer> friends = new HashSet<>();

    @PositiveOrZero
    private int id;
    @NotBlank(message = "Отсутствует email")
    @Email(message = "Некорректный email")
    private String email;
    @NotNull(message = "Отсутствует логин")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Некорректная дата рождения")
    private LocalDate birthday;
    private StatusRelation statusRelation;

}
