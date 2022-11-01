package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTests  {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    private Validator validator = factory.getValidator();

    UserController userController;

    User user;

    @BeforeEach
    void UserControllerInit() {
        userController = new UserController();
    }

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userController = new UserController();
    }

    @Test
    void userWithoutNameTest() {
        user = new User("test@test.ru", "login", LocalDate.of(1990, 05, 06));
        userController.validate(user);
        assertEquals("login", user.getName());

        user = new User("test@test.ru", "login", LocalDate.of(1990, 05, 06));
        user.setName(" ");
        userController.validate(user);
        assertEquals("login", user.getName());
    }

    @Test
    void duplicateUserTest() {
        user = new User("test@test.ru", "login", LocalDate.of(1990, 05, 06));
        user.setId(1);
        userController.users.put(user.getId(), user);
        assertThrows(ValidationException.class, () -> userController.validate(user));
    }

    @Test
    void emailBlancTest() {
        user = new User(null, "login", LocalDate.of(1990, 05, 06));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
        System.out.println(violations);
    }

    @Test
    void emailWithoutATTest() {
        user = new User("null", "login", LocalDate.of(1990, 05, 06));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
        System.out.println(violations);
    }

    @Test
    void emailNullTest() {
        user = new User(null, "login", LocalDate.of(1990, 05, 06));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void loginBlancTest() {
        user = new User("test@test.ru", " ", LocalDate.of(1990, 05, 06));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void loginNullTest() {
        user = new User("test@test.ru", null, LocalDate.of(1990, 05, 06));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void birthdateNullTest() {
        user = new User("test@test.ru", "login", null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void birthdateIncorrectTest() {
        user = new User("test@test.ru", "login", LocalDate.of(2022, 11, 02));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

}
