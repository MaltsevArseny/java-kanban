package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.storage.db")
class FilmDbStorageTest {

    @Autowired
    private FilmStorage filmStorage;

    FilmDbStorageTest(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Test
    void shouldCreateAndFindFilmById() {
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        mpa.setName("G");

        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(mpa)
                .build();

        Film createdFilm = filmStorage.create(film);
        assertThat(createdFilm).isNotNull();

        Optional<Film> foundFilm = filmStorage.findById(createdFilm.getId());
        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getName()).isEqualTo(film.getName());
    }

}