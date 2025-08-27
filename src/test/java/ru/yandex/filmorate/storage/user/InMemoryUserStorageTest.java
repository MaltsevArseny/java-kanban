package ru.yandex.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    private InMemoryUserStorage userStorage;
    private User testUser;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("test login");
        testUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateUser() {
        User createdUser = userStorage.create(testUser);

        assertNotNull(createdUser.getId());
        assertEquals("test login", createdUser.getLogin());
        assertEquals("test login", createdUser.getName()); // Name should be set to log in
        assertEquals(1, userStorage.findAll().size());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsNull() {
        testUser.setName(null);
        User createdUser = userStorage.create(testUser);

        assertEquals("test login", createdUser.getName());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {
        testUser.setName("   ");
        User createdUser = userStorage.create(testUser);

        assertEquals("test login", createdUser.getName());
    }

    @Test
    void shouldFindAllUsers() {
        userStorage.create(testUser);
        User anotherUser = new User();
        anotherUser.setEmail("another@mail.ru");
        anotherUser.setLogin("another login");
        anotherUser.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.create(anotherUser);

        List<User> users = userStorage.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void shouldUpdateUser() {
        User createdUser = userStorage.create(testUser);
        createdUser.setName("Updated Name");

        User updatedUser = userStorage.update(createdUser);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(1, userStorage.findAll().size());
    }

    @Test
    void shouldFindUserById() {
        User createdUser = userStorage.create(testUser);

        Optional<User> foundUser = userStorage.findById(createdUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(createdUser.getId(), foundUser.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> foundUser = userStorage.findById(999L);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void shouldDeleteUser() {
        User createdUser = userStorage.create(testUser);
        userStorage.delete(createdUser.getId());

        Optional<User> foundUser = userStorage.findById(createdUser.getId());
        assertFalse(foundUser.isPresent());
        assertTrue(userStorage.findAll().isEmpty());
    }
}