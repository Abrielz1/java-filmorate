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
import java.util.Collection;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.List;
import java.sql.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class InDbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAll() {
        final String sql = "select * from films";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film create(Film film) {
        final String sql = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        final String mpaSql = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
        jdbcTemplate.update(mpaSql, film.getId(), film.getMpa().getId());
        final String genresSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genresSql, film.getId(), g.getId());
            }
        }
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film update(Film film) {
        final String check = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(check, film.getId());
        if (!filmRows.next()) {
            log.warn("Фильм с id {} не найден", film.getId());
            throw new ObjectNotFoundException("Фильм не найден");
        }
        final String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?" +
                "WHERE id = ?";
        if (film.getMpa() != null) {
            final String deleteMpa = "DELETE FROM mpa_films WHERE film_id = ?";
            final String updateMpa = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteMpa, film.getId());
            jdbcTemplate.update(updateMpa, film.getId(), film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            final String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
            final String updateGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteGenres, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenres, film.getId(), g.getId());
                }
            }
        }
        jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getId());
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film getById(int id) {
        String check = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(check, id);
        if (!filmRows.next()) {
            log.warn("Фильм с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Фильм не найден");
        }
        final String sql = "SELECT * FROM films WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeFilm, id);
    }

    @Override
    public Film deleteById(int id) {
        Film film = getById(id);
        final String genresSql = "DELETE FROM film_genre WHERE film_id = ?";
        String mpaSql = "DELETE FROM mpa_films WHERE film_id = ?";
        jdbcTemplate.update(genresSql, id);
        jdbcTemplate.update(mpaSql, id);
        final String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        validate(filmId, userId);
        final String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        return getById(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        validate(filmId, userId);
        final String sql = "DELETE FROM films_likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);
        return getById(filmId);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        String sql = "SELECT id, name, description, release_date, duration " +
                "FROM films " +
                "LEFT JOIN films_likes fl ON films.id = fl.film_id " +
                "group by films.id, fl.film_id IN ( " +
                "    SELECT film_id " +
                "    FROM films_likes " +
                ") " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        final int id = rs.getInt("id");
        final String name = rs.getString("name");
        final String description = rs.getString("description");
        final LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");

        return new Film(id, name, description, releaseDate, duration, findMpa(id), findGenres(id));
    }

    private List<Genre> findGenres(int filmId) {
        final String genresSql = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN film_genre FG on genre.genre_id = FG.GENRE_ID " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(genresSql, this::makeGenre, filmId);
    }

    private Mpa findMpa(int filmId) {
        final String mpaSql = "SELECT id, name " +
                "FROM mpa " +
                "LEFT JOIN mpa_films MF ON mpa.id = mf.mpa_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.queryForObject(mpaSql, this::makeMpa, filmId);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        final int id = rs.getInt("genre_id");
        final String name = rs.getString("name");
        return new Genre(id, name);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        final int id = rs.getInt("id");
        final String name = rs.getString("name");
        return new Mpa(id, name);
    }

    private void validate(int filmId, int userId) {
        final String checkFilm = "SELECT * FROM films WHERE id = ?";
        final String checkUser = "SELECT * FROM users WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(checkFilm, filmId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUser, userId);
        if (!filmRows.next() || !userRows.next()) {
            log.warn("Фильм {} и(или) пользователь {} не найден.", filmId, userId);
            throw new ObjectNotFoundException("Фильм или пользователь не найдены");
        }
    }
}