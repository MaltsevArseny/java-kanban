package com.example.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    @Test
    void shouldCreateFilmWithAllFields() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        film.setDescription("A thief who steals corporate secrets");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        assertEquals(1L, film.getId());
        assertEquals("Inception", film.getName());
        assertEquals(148, film.getDuration());
    }
}