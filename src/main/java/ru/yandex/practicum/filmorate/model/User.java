package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class User {
    @NonNull
    int id;
    @NonNull
    @NotBlank
    @Email
    String email;
    @NonNull
    @NotBlank
    String login;
    @NonNull
    String name;
    @NonNull
    LocalDate birthday;
}
