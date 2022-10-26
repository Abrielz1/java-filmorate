package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    @NonNull
    int id;
    @NonNull
    String email;
    @NonNull
    String login;
    @NonNull
    String name;
    @NonNull
    LocalDate birthday;
}
