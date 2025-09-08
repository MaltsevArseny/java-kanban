package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
@SuppressWarnings("unused") // Подавляет предупреждения о неиспользуемых полях
class FilmorateIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    @Test
    void shouldCreateUserAndFilm() {
        User user = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userService.create(user);
        assertThat(createdUser).isNotNull();

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

        Film createdFilm = filmService.create(film);
        assertThat(createdFilm).isNotNull();

        filmService.addLike(createdFilm.getId(), createdUser.getId());

        Film filmWithLike = filmService.findById(createdFilm.getId()).orElseThrow();
        assertThat(filmWithLike.getLikes()).contains(createdUser.getId());
    }
}