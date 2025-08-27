package ru.yandex.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.storage.film.FilmStorage;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @InjectMocks
    private FilmService filmService;

    @Test
    void addLike_shouldAddLikeWhenFilmAndUserExist() {
        // given
        Film film = new Film();
        film.setId(1L);
        when(filmStorage.findById(1L)).thenReturn(Optional.of(film));

        // when
        filmService.addLike(1L, 1L);

        // then
        assertTrue(film.getLikes().contains(1L));
    }
}