package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void save_NewUser_ShouldAssignIdAndSave() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("John", savedUser.getName());
        assertEquals("john@test.com", savedUser.getEmail());
    }

    @Test
    void findById_ExistingUser_ShouldReturnUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        User savedUser = userRepository.save(user);

        User foundUser = userRepository.findById(savedUser.getId());

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals("John", foundUser.getName());
    }

    @Test
    void findById_NonExistingUser_ShouldReturnNull() {
        User foundUser = userRepository.findById(999L);

        assertNull(foundUser);
    }

    @Test
    void findAll_WithUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@test.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Jane");
        user2.setEmail("jane@test.com");
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void update_ExistingUser_ShouldUpdateUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        User savedUser = userRepository.save(user);

        User updateData = new User();
        updateData.setId(savedUser.getId());
        updateData.setName("John Updated");
        updateData.setEmail("updated@test.com");

        User updatedUser = userRepository.update(updateData);

        assertEquals("John Updated", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());
    }

    @Test
    void update_NonExistingUser_ShouldReturnNull() {
        User updateData = new User();
        updateData.setId(999L);
        updateData.setName("Test");

        User updatedUser = userRepository.update(updateData);

        assertNull(updatedUser);
    }

    @Test
    void deleteById_ExistingUser_ShouldRemoveUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        assertNull(userRepository.findById(savedUser.getId()));
    }

    @Test
    void existsById_ExistingUser_ShouldReturnTrue() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        User savedUser = userRepository.save(user);

        boolean exists = userRepository.existsById(savedUser.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_NonExistingUser_ShouldReturnFalse() {
        boolean exists = userRepository.existsById(999L);

        assertFalse(exists);
    }
}