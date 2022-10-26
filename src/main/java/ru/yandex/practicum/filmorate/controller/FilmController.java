package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")

public class FilmController {

    LocalDate date = LocalDate.of(1895,12,28);
    private int idCounter = 1;

    Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Количество фильмов {}", films.size() );
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        checkFilm(film);
        films.put(idCounter++, film);
        return film;
    }

    @RequestMapping
public Film put (@RequestBody Film film) {
        if (!films.containsKey(film.getId())) throw new ValidationException("Кина такого нет!");
        films.remove(film.getId());
        checkFilm(film);
        films.put(idCounter++, film);
        return film;
    }
    public void checkFilm (@RequestBody Film film) {

        if (film.getName().isBlank() || film.getDescription().length() > 200 )
            throw new ValidationException("Название фильма не указано или описани слишком длинное (200 символов)!");
        if (film.getReleaseDate().isBefore(date) || film.getDuration() < 0)
            throw new ValidationException("В то время кино не было (28 декабря 1895 года) или фильм идёт в обратную сторону");
    }
}
