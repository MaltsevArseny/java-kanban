package ru.yandex.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.service.UserService;
import jakarta.validation.Valid;
import java.util.*;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        User createdUser = userService.create(user);
        log.info("Пользователь создан успешно: {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        User updatedUser = userService.update(user);
        log.info("Пользователь обновлен успешно: {}", updatedUser);
        return updatedUser;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Получение пользователя по ID: {}", id);
        User user = userService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id=" + id + " не найден."));
        log.info("Пользователь найден: {}", user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление друга: пользователь ID {} добавляет друга ID {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Друг добавлен успешно");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаление друга: пользователь ID {} удаляет друга ID {}", id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Друг удален успешно");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получение списка друзей пользователя ID: {}", id);
        List<User> friends = userService.getFriends(id);
        log.info("Найдено {} друзей пользователя ID: {}", friends.size(), id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получение общих друзей пользователей ID: {} и ID: {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Найдено {} общих друзей", commonFriends.size());
        return commonFriends;
    }
}