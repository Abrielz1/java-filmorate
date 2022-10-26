package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        checkUsers(user);
        user.setId(userId++);

        users.put(user.getId(), user);
        log.info("Добавлен пользователь с логином {}", user.getLogin());
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        validate(user);
        if (!users.containsKey(user.getId()))
            throw new ValidationException("Пользователя не существует, необходима регистрация нового пользователя");
        users.remove(user.getId());
        checkUsers(user);
        users.put(user.getId(), user);
        log.info("Информация о пользователе {} обновлена", user.getLogin());
        return user;
    }

    private void validate(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@"))
            throw new ValidationException("Адрес электронной почты не может быть пустым и должен содержать \"@\"");
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("До 13 вход воспрещён");
    }

    private void checkUsers(@RequestBody User user) {
        Collection<User> userCollection = users.values();
        for (User us : userCollection) {
            if (user.getLogin().equals(us.getLogin()) || user.getEmail().equals(us.getEmail()) ) {
                throw new ValidationException("Пользователь с таким email или login уже существует");
            }
        }
    }
}