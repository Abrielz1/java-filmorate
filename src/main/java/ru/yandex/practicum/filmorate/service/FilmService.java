package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.storage.film.InDbFilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final InDbFilmStorage filmStorage;

    public Collection<Film> findAll() {
        log.info("Список фильмов отправлен");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public Film deleteById(int id) {
        return  filmStorage.deleteById(id);
    }

    public Film addLike(int filmId, int userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(int filmId, int userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }
}
