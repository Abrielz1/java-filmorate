package ru.yandex.practicum.filmorate.service;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import org.assertj.core.api.Assertions;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.util.Arrays;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaTest {
    private final MpaService mpaService;
    @Test
    public void testGetAllMpa() {
        Collection<Mpa> mpaRatingStorage = mpaService.findAll();
        Assertions.assertThat(mpaRatingStorage)
                .isNotEmpty()
                .extracting(Mpa::getName)
                .containsAll(Arrays.asList("G", "PG", "PG-13", "R", "NC-17"));
    }

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaService.getById(3);
        Assertions.assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("name", "PG-13");
    }
}
