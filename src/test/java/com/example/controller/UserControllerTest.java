package com.example.controller;

import com.example.FilmUserApplication;
import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FilmUserApplication.class
)
class UserControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TestRestTemplate restTemplate;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
    }

    @Test
    void shouldCreateUser() {
        ResponseEntity<User> response = restTemplate.postForEntity(
                "/users",
                testUser,
                User.class
        );

        // Проверка статуса
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Неверный статус код");

        // Проверка тела ответа
        User createdUser = response.getBody();
        assertNotNull(createdUser, "Тело ответа не должно быть null");
        assertNotNull(createdUser.getId(), "ID пользователя не должен быть null");
        assertEquals("Test User", createdUser.getName(), "Имя пользователя не совпадает");
        assertEquals("test@example.com", createdUser.getEmail(), "Email пользователя не совпадает");
    }

    @Test
    void shouldGetAllUsers() {
        // Создаем пользователя
        ResponseEntity<User> createResponse = restTemplate.postForEntity(
                "/users",
                testUser,
                User.class
        );
        assertEquals(HttpStatus.OK, createResponse.getStatusCode(), "Ошибка при создании пользователя");

        // Получаем всех пользователей
        ResponseEntity<User[]> response = restTemplate.getForEntity(
                "/users",
                User[].class
        );

        // Проверки
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Неверный статус код");
        User[] users = response.getBody();
        assertNotNull(users, "Список пользователей не должен быть null");
        assertTrue(users.length > 0, "Список пользователей должен содержать элементы");

        // Проверка первого пользователя
        User firstUser = users[0];
        assertNotNull(firstUser.getId(), "ID пользователя не должен быть null");
        assertEquals("Test User", firstUser.getName(), "Имя пользователя не совпадает");
    }
}