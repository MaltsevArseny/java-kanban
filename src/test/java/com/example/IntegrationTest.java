package com.example;

import com.example.model.Film;
import com.example.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TestRestTemplate restTemplate;

    IntegrationTest(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Test
    void shouldCreateUserAndFilm() {
        // 1. Тестирование создания пользователя
        User testUser = createTestUser();
        ResponseEntity<User> userResponse = createUser(testUser);
        validateUserResponse(userResponse, testUser);

        // 2. Тестирование создания фильма
        Film testFilm = createTestFilm();
        ResponseEntity<Film> filmResponse = createFilm(testFilm);
        validateFilmResponse(filmResponse, testFilm);
    }

    private User createTestUser() {
        User user = new User();
        user.setName("Integration User");
        user.setEmail("integration@test.com");
        return user;
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Integration Film");
        film.setDescription("Integration Test Description");
        return film;
    }

    private ResponseEntity<User> createUser(User user) {
        return restTemplate.postForEntity("/users", user, User.class);
    }

    private ResponseEntity<Film> createFilm(Film film) {
        return restTemplate.postForEntity("/films", film, Film.class);
    }

    private void validateUserResponse(ResponseEntity<User> response, User expectedUser) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUser.getName(), response.getBody().getName());
        assertEquals(expectedUser.getEmail(), response.getBody().getEmail());
    }

    private void validateFilmResponse(ResponseEntity<Film> response, Film expectedFilm) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedFilm.getName(), response.getBody().getName());
        assertEquals(expectedFilm.getDescription(), response.getBody().getDescription());
    }
}