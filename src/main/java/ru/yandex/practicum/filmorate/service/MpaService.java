package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa getById(int id) {
        return mpaStorage.getById(id);
    }
}
