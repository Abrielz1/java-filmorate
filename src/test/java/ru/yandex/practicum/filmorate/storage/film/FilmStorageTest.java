package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.AssertionsForClassTypes;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.*;
import org.assertj.core.api.Assertions;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InDbUserStorage;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {

    private final InDbFilmStorage inDbFilmStorage;

    private final InDbUserStorage inDbUserStorage;

    private Film film = Film.builder()
            .name("testFilm")
            .description("desc")
            .releaseDate(LocalDate.of(2020, 1, 1))
            .duration(110)
            .mpa(new Mpa(1, "G"))
            .genres(null)
            .build();

    @Test
    void addFilmTest() {
        inDbFilmStorage.create(film);
        AssertionsForClassTypes.assertThat(film).extracting("id").isNotNull();
        AssertionsForClassTypes.assertThat(film).extracting("name").isNotNull();
    }

    @Test
    void updateFilmTest() {
        inDbFilmStorage.create(film);
        film.setName("testUpdateFilm");
        film.setDescription("testUpdateDesc");
        inDbFilmStorage.update(film);
        AssertionsForClassTypes.assertThat(inDbFilmStorage.getById(film.getId()))
                .hasFieldOrPropertyWithValue("name", "testUpdateFilm")
                .hasFieldOrPropertyWithValue("description", "testUpdateDesc");

    }

    @Test
    void getFilmTest() {
        inDbFilmStorage.create(film);
        inDbFilmStorage.getById(film.getId());
        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("id", film.getId());
    }

    @Test
    void removeFilmTest() {
        inDbFilmStorage.create(film);
        inDbFilmStorage.deleteById(film.getId());
        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("id", film.getId());
    }

    @Test
    void updateFilmNotFoundTest() {
        Film film = Film.builder()
                .id(9999)
                .name("testFilm")
                .description(("desc"))
                .releaseDate(LocalDate.of(2020, 1, 1))
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();
        Assertions.assertThatThrownBy(() -> inDbFilmStorage.update(film))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void addLikeFilmTest() {
        User user = User.builder()
                .id(1)
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();

        Film film1 = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        inDbUserStorage.create(user);
        inDbFilmStorage.create(film1);
        System.out.println(user.getId() + " - Это юзер Id!");
        System.out.println(film1.getId() + " - Это фильм Id!");
        inDbFilmStorage.addLike(film1.getId(), user.getId());
        assertThat(inDbFilmStorage.getBestFilms(film1.getId()).isEmpty());
        assertThat(inDbFilmStorage.getBestFilms(film1.getId())).isNotNull();
        Assertions.assertThat(inDbFilmStorage.getBestFilms(film1.getId()).size() == 2);
    }

    @Test
    void removeFilmLikeTest() {
        User user1 = User.builder()
                .id(1)
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();

        Film film2 = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        inDbUserStorage.create(user1);
        inDbFilmStorage.create(film2);
        inDbFilmStorage.create(film2);
        inDbFilmStorage.addLike(film2.getId(), user1.getId());
        inDbFilmStorage.removeLike(film2.getId(), user1.getId());
        assertThat(inDbFilmStorage.getBestFilms(film2.getId()).isEmpty());
        assertThat(inDbFilmStorage.getBestFilms(film2.getId())).isNotNull();
        Assertions.assertThat(inDbFilmStorage.getBestFilms(film2.getId()).size() == 1);
    }

    @Test
    void getBestFilmTest() {

        Film film1 = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        Film film2 = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        inDbFilmStorage.create(film);
        inDbFilmStorage.create(film1);
        inDbFilmStorage.create(film2);

        User user = User.builder()
                .id(1)
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();

        User user1 = User.builder()
                .id(1)
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();

        User user2= User.builder()
                .id(1)
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();

        inDbUserStorage.create(user);
        inDbUserStorage.create(user1);
        inDbUserStorage.create(user2);

        inDbFilmStorage.addLike(film.getId(), user.getId());
        inDbFilmStorage.addLike(film1.getId(), user1.getId());
        inDbFilmStorage.addLike(film2.getId(), user2.getId());
        inDbFilmStorage.addLike(film.getId(), user1.getId());
        inDbFilmStorage.addLike(film.getId(), user2.getId());
        assertThat(inDbFilmStorage.getBestFilms(film.getId())).isNotNull();
        Assertions.assertThat(inDbFilmStorage.getBestFilms(film.getId()).size() == 6);
    }
}
