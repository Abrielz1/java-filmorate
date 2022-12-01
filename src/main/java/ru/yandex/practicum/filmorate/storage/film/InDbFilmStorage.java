package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InDbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAll() {
        final String SQL = "select * from films";
        return jdbcTemplate.query(SQL, this::makeFilm);
    }

    @Override
    public Film create(Film film) {
        final String SQL = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        final String MPASQL = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
        jdbcTemplate.update(MPASQL, film.getId(), film.getMpa().getId());
        final String GENRESSQL = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(GENRESSQL, film.getId(), g.getId());
            }
        }
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film update(Film film) {
        final String CHECK = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(CHECK, film.getId());
        if (!filmRows.next()) {
            log.warn("Фильм с id {} не найден", film.getId());
            throw new ObjectNotFoundException("Фильм не найден");
        }
        final String SQL = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?" +
                "WHERE id = ?";
        if (film.getMpa() != null) {
            final String DELETEMPA = "DELETE FROM mpa_films WHERE film_id = ?";
            final String UPDATEMPA = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
            jdbcTemplate.update(DELETEMPA, film.getId());
            jdbcTemplate.update(UPDATEMPA, film.getId(), film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            final String DELETEGENRES = "DELETE FROM film_genre WHERE film_id = ?";
            final String UPDATEGENRES = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(DELETEGENRES, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(UPDATEGENRES, film.getId(), g.getId());
                }
            }
        }
        jdbcTemplate.update(SQL,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getId());
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film getById(int id) {
        String CHECK = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(CHECK, id);
        if (!filmRows.next()) {
            log.warn("Фильм с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Фильм не найден");
        }
        final String SQL = "SELECT * FROM films WHERE id = ?";
        return jdbcTemplate.queryForObject(SQL, this::makeFilm, id);
    }

    @Override
    public Film deleteById(int id) {
        Film film = getById(id);
        final String genresSql = "DELETE FROM film_genre WHERE film_id = ?";
        String MPASQL = "DELETE FROM mpa_films WHERE film_id = ?";
        jdbcTemplate.update(genresSql, id);
        jdbcTemplate.update(MPASQL, id);
        final String SQL = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(SQL, id);
        return film;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        validate(filmId, userId);
        final String SQL = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(SQL, filmId, userId);
        return getById(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        validate(filmId, userId);
        final String SQL = "DELETE FROM films_likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(SQL, filmId, userId);
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);
        return getById(filmId);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        String SQL = "SELECT id, name, description, release_date, duration " +
                "FROM films " +
                "LEFT JOIN films_likes fl ON films.id = fl.film_id " +
                "group by films.id, fl.film_id IN ( " +
                "    SELECT film_id " +
                "    FROM films_likes " +
                ") " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(SQL, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        final int id = rs.getInt("id");
        final String NAME = rs.getString("name");
        final String DESCRIPTION = rs.getString("description");
        final LocalDate RELEASEdATE = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");

        return new Film(id, NAME, DESCRIPTION, RELEASEdATE, duration, findMpa(id), findGenres(id));
    }

    private List<Genre> findGenres(int filmId) {
        final String GENRESSQL = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN film_genre FG on genre.genre_id = FG.GENRE_ID " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(GENRESSQL, this::makeGenre, filmId);
    }

    private Mpa findMpa(int filmId) {
        final String MPASQL = "SELECT id, name " +
                "FROM mpa " +
                "LEFT JOIN mpa_films MF ON mpa.id = mf.mpa_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.queryForObject(MPASQL, this::makeMpa, filmId);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        final int ID = rs.getInt("genre_id");
        final String name = rs.getString("name");
        return new Genre(ID, name);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        final int ID = rs.getInt("id");
        final String name = rs.getString("name");
        return new Mpa(ID, name);
    }

    private void validate(int filmId, int userId) {
        final String CHECKFILM = "SELECT * FROM films WHERE id = ?";
        final String CHECKUSER = "SELECT * FROM users WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(CHECKFILM, filmId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(CHECKUSER, userId);
        if (!filmRows.next() || !userRows.next()) {
            log.warn("Фильм {} и(или) пользователь {} не найден.", filmId, userId);
            throw new ObjectNotFoundException("Фильм или пользователь не найдены");
        }
    }
}