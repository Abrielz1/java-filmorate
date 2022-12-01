package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.assertj.core.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.*;
import java.time.LocalDate;



@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTests {
    private final InDbUserStorage inDbUserStorage;

    @Test
    void AddUserTest() {
        User user = User.builder()
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        inDbUserStorage.create(user);
        AssertionsForClassTypes.assertThat(user).extracting("id").isNotNull();
        AssertionsForClassTypes.assertThat(user).extracting("name").isNotNull();
    }

    @Test
    void getAllUsersTest() {
        User user = User.builder()
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        inDbUserStorage.create(user);
        Collection<User> users = inDbUserStorage.findAll();
        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThat(users).isNotEmpty().isNotNull().hasSize(1).doesNotHaveDuplicates();
        Assertions.assertThat(users).extracting("email").contains(user.getEmail());
        Assertions.assertThat(users).extracting("login").contains(user.getLogin());
    }

    @Test
    void findUserByIdTest() {
        User user = User.builder()
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        inDbUserStorage.create(user);
        inDbUserStorage.getById(user.getId());
        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("id", user.getId());
    }

    @Test
    void updateUserByIdTest() {
        User user = User.builder()
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        inDbUserStorage.create(user);
        user.setName("testUpdatedName");
        user.setLogin("testUpdatedLogin");
        user.setEmail("updatedExample@mail.mail");
        inDbUserStorage.update(user);
        AssertionsForClassTypes.assertThat(inDbUserStorage.getById(user.getId()))
                .hasFieldOrPropertyWithValue("login", "testUpdatedLogin")
                .hasFieldOrPropertyWithValue("name", "testUpdatedName")
                .hasFieldOrPropertyWithValue("email", "updatedExample@mail.mail");
    }

    @Test
    void removeUserByIdTest() {
        User user = User.builder()
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        inDbUserStorage.create(user);
        Collection<User> users = inDbUserStorage.findAll();
        inDbUserStorage.deleteById(user.getId());
        Assertions.assertThat(users).hasSize(1);
        Assertions.assertThatThrownBy(()->inDbUserStorage.getById(user.getId()))
                .isInstanceOf(ObjectNotFoundException.class);
    }








}





//    private InMemoryUserStorage userStorage;
//    User user;
//
//    @BeforeEach
//    void init() {
//        userStorage = new InMemoryUserStorage();
//    }
//
//    @Test
//    void userWithoutNameTest() {
//        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
//        userStorage.validate(user);
//        assertEquals("login", user.getName());
//
//        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
//        user.setName(" ");
//        userStorage.validate(user);
//        assertEquals("login", user.getName());
//    }
//.getUsers().put(user.getId(), user);
//    @Test
//    void duplicateUserTest() {
//        user = new User("test@test.ru", "login", LocalDate.of(1990, 5, 6));
//        user.setId(1);
//        userStorage.getUsers().put(user.getId(), user);
//        assertThrows(InternalException.class, () -> userStorage.checkUsers(user));
//    }