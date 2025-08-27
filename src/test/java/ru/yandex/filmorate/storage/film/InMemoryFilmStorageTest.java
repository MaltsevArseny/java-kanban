package ru.yandex.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.filmorate.model.Film;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    private InMemoryFilmStorage filmStorage;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(1995, 12, 27));
        testFilm.setDuration(120);
    }

    @Test
    void shouldCreateFilm() {
        Film createdFilm = filmStorage.create(testFilm);

        assertNotNull(createdFilm.getId());
        assertEquals("Test Film", createdFilm.getName());
        assertEquals(1, filmStorage.findAll().size());
    }

    @Test
    void shouldFindAllFilms() {
        filmStorage.create(testFilm);
        Film anotherFilm = new Film();
        anotherFilm.setName("Another Film");
        anotherFilm.setDescription("Another Description");
        anotherFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        anotherFilm.setDuration(90);
        filmStorage.create(anotherFilm);

        List<Film> films = filmStorage.findAll();
        assertEquals(2, films.size());
    }

    @Test
    void shouldUpdateFilm() {
        Film createdFilm = filmStorage.create(testFilm);
        createdFilm.setName("Updated Film");

        Film updatedFilm = filmStorage.update(createdFilm);

        assertEquals("Updated Film", updatedFilm.getName());
        assertEquals(1, filmStorage.findAll().size());
    }

    @Test
    void shouldFindFilmById() {
        Film createdFilm = filmStorage.create(testFilm);

        Optional<Film> foundFilm = filmStorage.findById(createdFilm.getId());

        assertTrue(foundFilm.isPresent());
        assertEquals(createdFilm.getId(), foundFilm.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenFilmNotFound() {
        Optional<Film> foundFilm = filmStorage.findById(999L);
        assertFalse(foundFilm.isPresent());
    }

    @Test
    void shouldDeleteFilm() {
        Film createdFilm = filmStorage.create(testFilm);
        filmStorage.delete(createdFilm.getId());

        Optional<Film> foundFilm = filmStorage.findById(createdFilm.getId());
        assertFalse(foundFilm.isPresent());
        assertTrue(filmStorage.findAll().isEmpty());
    }
}