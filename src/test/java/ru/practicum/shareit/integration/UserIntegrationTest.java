package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        User user = new User();
        user.setName("Integration User");
        user.setEmail("integration@test.com");

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("Integration User", createdUser.getName());
        assertEquals("integration@test.com", createdUser.getEmail());
    }

    @Test
    void getUserById_AfterCreation_ShouldReturnUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        User createdUser = userService.createUser(user);

        User foundUser = userService.getUserById(createdUser.getId());

        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("Test User", foundUser.getName());
    }

    @Test
    void getAllUsers_WithMultipleUsers_ShouldReturnAll() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@test.com");
        userService.createUser(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@test.com");
        userService.createUser(user2);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void updateUser_ShouldModifyUser() {
        User user = new User();
        user.setName("Original");
        user.setEmail("original@test.com");
        User createdUser = userService.createUser(user);

        User updateData = new User();
        updateData.setName("Updated");
        updateData.setEmail("updated@test.com");

        User updatedUser = userService.updateUser(createdUser.getId(), updateData);

        assertEquals("Updated", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());
    }

    @Test
    void deleteUser_ShouldRemoveUser() {
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@test.com");
        User createdUser = userService.createUser(user);

        userService.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(createdUser.getId()));
    }
}