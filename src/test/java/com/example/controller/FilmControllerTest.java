package com.example.controller;

import com.example.FilmUserApplication;
import com.example.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FilmUserApplication.class
)
class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.now().minusYears(1));
        testFilm.setDuration(120);
    }

    @Test
    void shouldAddFilm() {
        // Используем restTemplate для отправки запроса
        ResponseEntity<Film> response = this.restTemplate.postForEntity(
                "/films",
                testFilm,
                Film.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Film createdFilm = response.getBody();
        assertNotNull(createdFilm.getId());
        assertEquals("Test Film", createdFilm.getName());
        assertEquals("Test Description", createdFilm.getDescription());
        assertEquals(120, createdFilm.getDuration());
    }

    @Test
    void shouldGetAllFilms() {
        // Используем restTemplate для создания фильма
        this.restTemplate.postForEntity("/films", testFilm, Film.class);

        // Используем restTemplate для получения списка
        ResponseEntity<Film[]> response = this.restTemplate.getForEntity(
                "/films",
                Film[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }
}