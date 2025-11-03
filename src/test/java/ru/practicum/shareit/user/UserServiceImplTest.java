package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
    }

    @Test
    void createUser_ValidUser_ShouldReturnSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("John Doe", createdUser.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_InvalidEmail_ShouldThrowException() {
        user.setEmail("invalid-email");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_EmptyName_ShouldThrowException() {
        user.setName("");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_EmptyEmail_ShouldThrowException() {
        user.setEmail("");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(user);

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_ValidUpdate_ShouldReturnUpdatedUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original");
        existingUser.setEmail("original@test.com");

        User updateData = new User();
        updateData.setName("Updated");
        updateData.setEmail("updated@test.com");

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1L, updateData);

        assertEquals("Updated", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());
        verify(userRepository, times(1)).update(existingUser);
    }

    @Test
    void updateUser_PartialUpdate_ShouldReturnPartiallyUpdatedUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original");
        existingUser.setEmail("original@test.com");

        User updateData = new User();
        updateData.setName("Updated");

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1L, updateData);

        assertEquals("Updated", updatedUser.getName());
        assertEquals("original@test.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_NonExistingUser_ShouldThrowException() {
        User updateData = new User();
        updateData.setName("Updated");

        when(userRepository.findById(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.updateUser(999L, updateData));
        verify(userRepository, never()).update(any());
    }

    @Test
    void deleteUser_ExistingUser_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_ExistingUser_ShouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean exists = userService.existsById(1L);

        assertTrue(exists);
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_NonExistingUser_ShouldReturnFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean exists = userService.existsById(999L);

        assertFalse(exists);
        verify(userRepository, times(1)).existsById(999L);
    }
}