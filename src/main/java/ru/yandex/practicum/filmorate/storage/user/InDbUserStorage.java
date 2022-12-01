package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.sql.PreparedStatement;
import lombok.extern.slf4j.Slf4j;
import java.sql.SQLException;
import java.util.Collection;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.util.List;
import java.sql.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class InDbUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAll() {
        final String SQL = "SELECT * FROM users";
        log.info("Список пользователей отправлен");
        return jdbcTemplate.query(SQL, this::makeUser);
    }

    @Override
    public User create(User user) {
        final String SQL = "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(SQL, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        log.info("Пользователь с id {} отправлен", user.getId());
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        final String CHECK = "SELECT * FROM users WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(CHECK, user.getId());
        if (!filmRows.next()) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        final String SQL = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(SQL,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Пользователь {} обновлен", user.getId());
        return user;
    }

    @Override
    public User getById(int id) {
        final String CHECK = "SELECT * FROM users WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(CHECK, id);
        if (!filmRows.next()) {
            log.warn("Пользователь с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        final String SQL = "select * from users where id = ?";
        log.info("Пользователь с id {} отправлен", id);
        return jdbcTemplate.queryForObject(SQL, this::makeUser, id);
    }

    @Override
    public User deleteById(int id) {
        final String SQL = "DELETE FROM users WHERE id = ?";
        User user = getById(id);
        jdbcTemplate.update(SQL, id);
        log.info("Пользователь с id {} удален", id);
        return user;
    }

    @Override
    public List<Integer> addFriendship(int followedId, int followerId) {
        validate(followedId, followerId);
        final String SQLFORWRITE = "INSERT INTO mutual_friendship (user_id, friend_id, status) " +
                "VALUES (?, ?, ?)";
        final String SQLFORUPDATE = "UPDATE mutual_friendship SET status = ? " +
                "WHERE user_id = ? AND friend_id = ?";
        final String CHECKMUTUAL = "SELECT * FROM mutual_friendship WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(CHECKMUTUAL, followedId, followerId);
        if (userRows.first()) {
            jdbcTemplate.update(SQLFORUPDATE, FriendshipStatus.CONFIRMED.toString(), followedId, followerId);
        } else {
            jdbcTemplate.update(SQLFORWRITE, followedId, followerId, FriendshipStatus.REQUIRED.toString());
        }
        log.info("Пользователь {} подписался на {}", followedId, followerId);
        return List.of(followedId, followerId);
    }

    @Override
    public List<Integer> removeFriendship(int followedId, int followerId) {
        final String SQL = "DELETE FROM mutual_friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(SQL, followedId, followerId);
        log.info("Пользователь {} отписался от {}", followerId, followedId);
        return List.of(followedId, followerId);
    }

    @Override
    public List<User> getFriendsListById(int id) {
        final String CHECK = "SELECT * FROM users WHERE id = ?";
        SqlRowSet followingRow = jdbcTemplate.queryForRowSet(CHECK, id);
        if (!followingRow.next()) {
            log.warn("Пользователь с id {} не найден", id);
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        final String SQL = "SELECT id, email, login, name, birthday " +
                "FROM USERS " +
                "LEFT JOIN mutual_friendship mf on users.id = mf.friend_id " +
                "where user_id = ? AND status LIKE 'REQUIRED'";
        log.info("Запрос получения списка друзей пользователя {} выполнен", id);
        return jdbcTemplate.query(SQL, this::makeUser, id);
    }

    @Override
    public List<User> getCommonFriendsList(int followedId, int followerId) {
        validate(followedId, followerId);
        final String SQL = "SELECT id, email, login, name, birthday " +
                "FROM mutual_friendship AS mf " +
                "LEFT JOIN users u ON u.id = mf.friend_id " +
                "WHERE mf.user_id = ? AND mf.friend_id IN ( " +
                "SELECT friend_id " +
                "FROM mutual_friendship AS mf " +
                "LEFT JOIN users AS u ON u.id = mf.friend_id " +
                "WHERE mf.user_id = ? )";
        log.info("Список общих друзей {} и {} отправлен", followedId, followerId);
        return jdbcTemplate.query(SQL, this::makeUser, followedId, followerId);
    }

    public User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    private void validate(int followedId, int followerId) {
        final String CHECK = "SELECT * FROM users WHERE id = ?";
        SqlRowSet followingRow = jdbcTemplate.queryForRowSet(CHECK, followedId);
        SqlRowSet followerRow = jdbcTemplate.queryForRowSet(CHECK, followerId);
        if (!followingRow.next() || !followerRow.next()) {
            log.warn("Пользователи с id {} и {} не найдены", followedId, followerId);
            throw new ObjectNotFoundException("Пользователи не найдены");
        }
    }
}
