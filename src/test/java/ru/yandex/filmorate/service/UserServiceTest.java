package ru.yandex.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setEmail("user1@mail.ru");
        testUser1.setLogin("user1");
        testUser1.setBirthday(LocalDate.of(2000, 1, 1));

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setEmail("user2@mail.ru");
        testUser2.setLogin("user2");
        testUser2.setBirthday(LocalDate.of(1990, 1, 1));

        testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setEmail("user3@mail.ru");
        testUser3.setLogin("user3");
        testUser3.setBirthday(LocalDate.of(1980, 1, 1));
    }

    @Test
    void shouldAddFriend() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userStorage.findById(2L)).thenReturn(Optional.of(testUser2));

        userService.addFriend(1L, 2L);

        assertTrue(testUser1.getFriends().contains(2L));
        assertTrue(testUser2.getFriends().contains(1L)); // Friendship should be mutual
        verify(userStorage, times(2)).findById(anyLong());
    }

    @Test
    void shouldRemoveFriend() {
        testUser1.getFriends().add(2L);
        testUser2.getFriends().add(1L);

        when(userStorage.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userStorage.findById(2L)).thenReturn(Optional.of(testUser2));

        userService.removeFriend(1L, 2L);

        assertFalse(testUser1.getFriends().contains(2L));
        assertFalse(testUser2.getFriends().contains(1L));
        verify(userStorage, times(2)).findById(anyLong());
    }

    @Test
    void shouldGetFriends() {
        testUser1.getFriends().addAll(List.of(2L, 3L));
        when(userStorage.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userStorage.findById(2L)).thenReturn(Optional.of(testUser2));
        when(userStorage.findById(3L)).thenReturn(Optional.of(testUser3));

        List<User> friends = userService.getFriends(1L);

        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(u -> u.getId().equals(2L)));
        assertTrue(friends.stream().anyMatch(u -> u.getId().equals(3L)));
    }

    @Test
    void shouldGetCommonFriends() {
        testUser1.getFriends().addAll(List.of(2L, 3L));
        testUser2.getFriends().addAll(List.of(1L, 3L));

        when(userStorage.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userStorage.findById(2L)).thenReturn(Optional.of(testUser2));
        when(userStorage.findById(3L)).thenReturn(Optional.of(testUser3));

        List<User> commonFriends = userService.getCommonFriends(1L, 2L);

        assertEquals(1, commonFriends.size());
        assertEquals(3L, commonFriends.getFirst().getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonFriends() {
        testUser1.getFriends().add(2L);
        testUser2.getFriends().add(1L);

        when(userStorage.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userStorage.findById(2L)).thenReturn(Optional.of(testUser2));

        List<User> commonFriends = userService.getCommonFriends(1L, 2L);

        assertTrue(commonFriends.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForAddFriend() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.addFriend(1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenFriendNotFoundForAddFriend() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userStorage.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.addFriend(1L, 2L));
    }
}