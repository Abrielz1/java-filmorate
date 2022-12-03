package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.AssertionsForClassTypes;
import ru.yandex.practicum.filmorate.model.User;
import org.assertj.core.api.Assertions;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTests {

    private final InDbUserStorage inDbUserStorage;

    @Test
    void addUserTest() {
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
    void findUserByIdTest() {
        User user = User.builder()
                .email("example@mail.mail")
                .login("login")
                .name("Doe")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();

        inDbUserStorage.create(user);
        AssertionsForClassTypes.assertThat(inDbUserStorage.getById(user.getId())).hasFieldOrPropertyWithValue("id", user.getId());
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
    public void testUpdateUserNotFound() {
        User user = User.builder()
                .id(9999)
                .login("testName")
                .email("example@mail.mail")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        Assertions.assertThatThrownBy(() -> inDbUserStorage.update(user))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void addFriendshipTest() {
        User friend = User.builder()
                .email("example_friend@mail.mail")
                .login("friend")
                .name("Dow")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        User follower = User.builder()
                .email("example_followerd@mail.mail")
                .login("follower")
                .name("Doe")
                .birthday(LocalDate.of(2000, 10, 20))
                .build();

        inDbUserStorage.create(friend);
        inDbUserStorage.create(follower);
        assertThat(inDbUserStorage.getFriendsListById(friend.getId()).isEmpty());
        inDbUserStorage.addFriendship(friend.getId(), follower.getId());
        assertThat(inDbUserStorage.getFriendsListById(friend.getId())).isNotNull();
        Assertions.assertThat(inDbUserStorage.getFriendsListById(friend.getId()).size() == 2);
    }

    @Test
    void removeFriendshipTest() {
        User friend = User.builder()
                .email("example_friend@mail.mail")
                .login("friend")
                .name("Dow")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        User follower = User.builder()
                .email("example_followerd@mail.mail")
                .login("follower")
                .name("Doe")
                .birthday(LocalDate.of(2000, 10, 20))
                .build();

        inDbUserStorage.create(friend);
        inDbUserStorage.create(follower);
        assertThat(inDbUserStorage.getFriendsListById(friend.getId()).isEmpty());
        inDbUserStorage.addFriendship(friend.getId(), follower.getId());
        assertThat(inDbUserStorage.getFriendsListById(friend.getId())).isNotNull();
        Assertions.assertThat(inDbUserStorage.getFriendsListById(friend.getId()).size() == 2);
        inDbUserStorage.removeFriendship(friend.getId(), follower.getId());
        Assertions.assertThat(inDbUserStorage.getFriendsListById(friend.getId()).size() == 1);
    }

    @Test
    void getFriendshipTest() {
        User friend = User.builder()
                .email("example_friend@mail.mail")
                .login("friend")
                .name("Dow")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        User follower = User.builder()
                .email("example_followerd@mail.mail")
                .login("follower")
                .name("Doe")
                .birthday(LocalDate.of(2000, 10, 20))
                .build();
        inDbUserStorage.create(friend);
        inDbUserStorage.create(follower);
        assertThat(inDbUserStorage.getFriendsListById(friend.getId()).isEmpty());
        inDbUserStorage.addFriendship(friend.getId(), follower.getId());
        Assertions.assertThat(inDbUserStorage.getFriendsListById(friend.getId()).size() == 2);
    }

    @Test
    void getCommonFriendshipTest() {
        User friend = User.builder()
                .email("example_friend@mail.mail")
                .login("friend")
                .name("Dow")
                .birthday(LocalDate.of(2000, 12, 22))
                .build();
        User follower = User.builder()
                .email("example_followerd@mail.mail")
                .login("follower")
                .name("Doe")
                .birthday(LocalDate.of(2000, 10, 20))
                .build();
        User following = User.builder()
                .email("example_followingd@mail.mail")
                .login("following")
                .name("Dire")
                .birthday(LocalDate.of(2000, 11, 21))
                .build();

        inDbUserStorage.create(friend);
        inDbUserStorage.create(follower);
        inDbUserStorage.create(following);
        inDbUserStorage.addFriendship(friend.getId(), following.getId());
        inDbUserStorage.addFriendship(follower.getId(), following.getId());
        Assertions.assertThat(inDbUserStorage.getCommonFriendsList(friend.getId(), follower.getId()).size() == 1);
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
        Assertions.assertThat(users).isNotEmpty().isNotNull().doesNotHaveDuplicates();
        Assertions.assertThat(users).extracting("email").contains(user.getEmail());
        Assertions.assertThat(users).extracting("login").contains(user.getLogin());
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
        inDbUserStorage.deleteById(user.getId());
        Assertions.assertThatThrownBy(()->inDbUserStorage.getById(user.getId()))
                .isInstanceOf(ObjectNotFoundException.class);
    }
}
