package ru.yandex.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.service.FilmService;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private final MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    // Конструктор с аннотацией @Autowired для внедрения зависимостей
    @Autowired
    FilmControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, FilmService filmService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.filmService = filmService;
    }

    @Test
    void shouldCreateFilm() throws Exception {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1995, 12, 27));
        film.setDuration(120);

        when(filmService.create(any(Film.class))).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Film"));
    }

    @Test
    void shouldReturnBadRequestWhenFilmInvalid() throws Exception {
        Film film = new Film(); // Invalid film without name

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllFilms() throws Exception {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test Film");

        when(filmService.findAll()).thenReturn(List.of(film));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Film"));
    }

    @Test
    void shouldGetFilmById() throws Exception {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test Film");

        when(filmService.findById(1L)).thenReturn(java.util.Optional.of(film));

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Film"));
    }

    @Test
    void shouldReturnNotFoundWhenFilmNotExists() throws Exception {
        when(filmService.findById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/films/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddLike() throws Exception {
        doNothing().when(filmService).addLike(1L, 1L);

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        verify(filmService, times(1)).addLike(1L, 1L);
    }

    @Test
    void shouldRemoveLike() throws Exception {
        doNothing().when(filmService).removeLike(1L, 1L);

        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());

        verify(filmService, times(1)).removeLike(1L, 1L);
    }

    @Test
    void shouldGetPopularFilms() throws Exception {
        Film film = new Film();
        film.setId(1L);
        film.setName("Popular Film");

        when(filmService.getPopularFilms(5)).thenReturn(List.of(film));

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Popular Film"));
    }
}