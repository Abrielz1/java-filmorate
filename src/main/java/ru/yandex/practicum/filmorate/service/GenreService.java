package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.storage.genre.InDbGenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final InDbGenreStorage inDbGenreStorage;

    public Collection<Genre> findAll() {
        return inDbGenreStorage.findAll();
    }

    public Genre getById(int id) {
        return inDbGenreStorage.getById(id);
    }
}
