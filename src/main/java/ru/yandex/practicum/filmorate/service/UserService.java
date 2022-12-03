package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        return userStorage.update(user);
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public User deleteById(int id) {
        return userStorage.deleteById(id);

    }

    public List<Integer> addFriendship(int firstId, int secondId) {
        return userStorage.addFriendship(firstId, secondId);
    }

    public List<Integer> removeFriendship(int firstId, int secondId) {
        return userStorage.removeFriendship(firstId, secondId);
    }

    public List<User> getFriendsListById(int id) {
        return userStorage.getFriendsListById(id);
    }

    public List<User> getCommonFriendsList(int firstId, int secondId) {
        return userStorage.getCommonFriendsList(firstId, secondId);
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }
}
