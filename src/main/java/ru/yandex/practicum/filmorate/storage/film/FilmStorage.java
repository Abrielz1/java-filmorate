package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film getById(int id);

    Film deleteById(int id);

    Film addLike(int filmId, int userId);

    Film removeLike(int filmId, int userId);

    List<Film> getBestFilms(int count);
}
