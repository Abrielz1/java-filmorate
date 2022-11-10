package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    private int filmId = 1;

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        checkFilms(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен в коллекцию", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ObjectNotFoundException("Такого фильма нет");
        }
        films.put(film.getId(), film);
        log.info("Информация о фильме {} обновлена", film.getName());
        return film;
    }

    @Override
    public Film getById(int id) {
        return films.get(id);
    }

    @Override
    public Film deleteById(int id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }

    public void checkFilms(Film film) {
        if (check(film)) {
            throw new InternalException("Такой фильм уже есть");
        }
    }

    private boolean check(Film film) {
       return findAll().
                stream().
                anyMatch(film1 -> film1.getName().equals(film.getName())
                        && film1.getReleaseDate().equals(film.getReleaseDate()));
    }
}