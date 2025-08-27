package ru.yandex.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        log.debug("Получение всех пользователей из хранилища");
        return userStorage.findAll();
    }

    public User create(User user) {
        log.debug("Создание пользователя в хранилище: {}", user);
        User createdUser = userStorage.create(user);
        log.debug("Пользователь создан в хранилище: {}", createdUser);
        return createdUser;
    }

    public User update(User user) {
        log.debug("Обновление пользователя в хранилище: {}", user);
        User updatedUser = userStorage.update(user);
        log.debug("Пользователь обновлен в хранилище: {}", updatedUser);
        return updatedUser;
    }

    public Optional<User> findById(Long id) {
        log.debug("Поиск пользователя по ID: {}", id);
        return userStorage.findById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Добавление дружбы: пользователь ID {} -> друг ID {}", userId, friendId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.debug("Дружба установлена. У пользователя ID: {} теперь {} друзей, у пользователя ID: {} теперь {} друзей",
                userId, user.getFriends().size(), friendId, friend.getFriends().size());
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Удаление дружбы: пользователь ID {} -> друг ID {}", userId, friendId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.debug("Дружба удалена. У пользователя ID: {} теперь {} друзей, у пользователя ID: {} теперь {} друзей",
                userId, user.getFriends().size(), friendId, friend.getFriends().size());
    }

    public List<User> getFriends(Long userId) {
        log.debug("Получение списка друзей пользователя ID: {}", userId);
        User user = getUserOrThrow(userId);
        List<User> friends = user.getFriends().stream()
                .map(friendId -> userStorage.findById(friendId).orElseThrow(() -> {
                    log.error("Друг с id={} не найден", friendId);
                    return new NoSuchElementException("Друг с id=" + friendId + " не найден.");
                }))
                .collect(Collectors.toList());
        log.debug("Найдено {} друзей пользователя ID: {}", friends.size(), userId);
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Поиск общих друзей пользователей ID: {} и ID: {}", userId, otherId);
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        List<User> commonFriends = commonFriendIds.stream()
                .map(friendId -> userStorage.findById(friendId).orElseThrow(() -> {
                    log.error("Общий друг с id={} не найден", friendId);
                    return new NoSuchElementException("Общий друг с id=" + friendId + " не найден.");
                }))
                .collect(Collectors.toList());

        log.debug("Найдено {} общих друзей пользователей ID: {} и ID: {}", commonFriends.size(), userId, otherId);
        return commonFriends;
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с id={} не найден", userId);
            return new NoSuchElementException("Пользователь с id=" + userId + " не найден.");
        });
    }
}