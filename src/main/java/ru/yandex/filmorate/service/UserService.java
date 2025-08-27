package ru.yandex.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Optional<User> findById(Long id) {
        return userStorage.findById(id);
    }

    // Новая бизнес-логика: Управление друзьями
    public void addFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId); // Дружба взаимная
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUserOrThrow(userId);
        return user.getFriends().stream()
                .map(friendId -> userStorage.findById(friendId).orElseThrow(() -> new NoSuchElementException("Друг с id=" + friendId + " не найден.")))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(friendId -> userStorage.findById(friendId).orElseThrow(() -> new NoSuchElementException("Общий друг с id=" + friendId + " не найден.")))
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь с id=" + userId + " не найден."));
    }
}