package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.sql.SQLException;
import java.util.Collection;
import java.sql.ResultSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final JdbcTemplate jdbcTemplate;

    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    public Mpa getById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!mpaRows.next()) {
            log.warn("Рейтинг {} не найден.", id);
            throw new ObjectNotFoundException("Рейтинг не найден");
        }
        return jdbcTemplate.queryForObject(sql, this::makeMpa, id);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
