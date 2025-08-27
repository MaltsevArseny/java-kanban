package ru.yandex.filmorate.model;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final Validator validator;

    public UserTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("test login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("test login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertEquals("test login", user.getName());
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("test login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoginWithSpaces() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("test login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("test login");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}