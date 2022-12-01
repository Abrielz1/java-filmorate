package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.sql.SQLException;
import java.util.Collection;
import java.sql.ResultSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final JdbcTemplate jdbcTemplate;

    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    public Genre getById(int id) {
        final String SQL = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(SQL, id);
        if (!genreRows.next()) {
            log.warn("Жанр {} не найден.", id);
            throw new ObjectNotFoundException("Жанр не найден");
        }
        return jdbcTemplate.queryForObject(SQL, this::makeGenre, id);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
