package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import ru.yandex.practicum.filmorate.storage.user.InDbUserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.AssertionsForClassTypes;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import org.assertj.core.api.Assertions;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
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
        AssertionsForClassTypes.assertThat(inDbFilmStorage.getById(film.getId())).hasFieldOrPropertyWithValue("id", film.getId());
    }

    @Test
    void removeFilmTest() {
        inDbFilmStorage.create(film);
        inDbFilmStorage.deleteById(film.getId());
        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("id", film.getId());
    }

    @Test
    void updateFilmNotFoundTest() {
        Film filmForUpdate = Film.builder()
                .id(9999)
                .name("testFilm")
                .description(("desc"))
                .releaseDate(LocalDate.of(2020, 1, 1))
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();
        Assertions.assertThatThrownBy(() -> inDbFilmStorage.update(filmForUpdate))
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

        Film filmForLike = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        inDbUserStorage.create(user);
        inDbFilmStorage.create(filmForLike);
        System.out.println(user.getId() + " - Это юзер Id!");
        System.out.println(filmForLike.getId() + " - Это фильм Id!");
        inDbFilmStorage.addLike(filmForLike.getId(), user.getId());
        assertThat(inDbFilmStorage.getBestFilms(filmForLike.getId()).isEmpty());
        assertThat(inDbFilmStorage.getBestFilms(filmForLike.getId())).isNotNull();
        Assertions.assertThat(inDbFilmStorage.getBestFilms(filmForLike.getId()).size() == 2);
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

        Film filmForLike = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        inDbUserStorage.create(user1);
        inDbFilmStorage.create(filmForLike);
        inDbFilmStorage.create(filmForLike);
        inDbFilmStorage.addLike(filmForLike.getId(), user1.getId());
        inDbFilmStorage.removeLike(filmForLike.getId(), user1.getId());
        assertThat(inDbFilmStorage.getBestFilms(filmForLike.getId()).isEmpty());
        assertThat(inDbFilmStorage.getBestFilms(filmForLike.getId())).isNotNull();
        Assertions.assertThat(inDbFilmStorage.getBestFilms(filmForLike.getId()).size() == 1);
    }

    @Test
    void getBestFilmTest() {

        Film filmForLike = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        Film otherFilmForLike = Film.builder()
                .name("testFilm")
                .description("desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(110)
                .mpa(new Mpa(1, "G"))
                .genres(null)
                .build();

        inDbFilmStorage.create(film);
        inDbFilmStorage.create(filmForLike);
        inDbFilmStorage.create(otherFilmForLike);

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
        inDbFilmStorage.addLike(filmForLike.getId(), user1.getId());
        inDbFilmStorage.addLike(otherFilmForLike.getId(), user2.getId());
        inDbFilmStorage.addLike(film.getId(), user1.getId());
        inDbFilmStorage.addLike(film.getId(), user2.getId());
        assertThat(inDbFilmStorage.getBestFilms(film.getId())).isNotNull();
        Assertions.assertThat(inDbFilmStorage.getBestFilms(film.getId()).size() == 6);
    }
}
