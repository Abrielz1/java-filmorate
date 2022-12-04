package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.sql.SQLException;
import java.util.Collection;
import java.sql.ResultSet;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InDbMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "SELECT * FROM mpa";

        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    @Override
    public Mpa getById(int id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!mpaRows.next()) {
            log.warn("Рейтинг {} не найден.", id);
            throw new ObjectNotFoundException("Рейтинг не найден");
        }

        return jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, id);
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String nameMpa = resultSet.getString("name");

        return new Mpa(id, nameMpa);
    }
}