package ru.yandex.filmorate.model;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    private final Validator validator;

    public FilmTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1995, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1995, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(1995, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1894, 12, 28));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDurationNegative() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1995, 12, 27));
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}