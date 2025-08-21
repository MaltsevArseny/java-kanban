package com.example.controller;

import com.example.model.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/users")
@Slf4j
@SuppressWarnings("unused")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        user.setId(counter.incrementAndGet());
        users.add(user);
        log.info("Создан пользователь: ID={}, Email={}", user.getId(), user.getEmail());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        Optional<User> existingUserOpt = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        if (existingUserOpt.isEmpty()) {
            log.warn("Попытка обновления несуществующего пользователя с ID={}", id);
            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOpt.get();
        updateUserFields(existingUser, updatedUser);

        log.info("Обновлен пользователь: ID={}, Email={}", existingUser.getId(), existingUser.getEmail());
        return ResponseEntity.ok(existingUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Запрошен список всех пользователей (количество: {})", users.size());
        return ResponseEntity.ok(new ArrayList<>(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        if (userOpt.isEmpty()) {
            log.warn("Запрос несуществующего пользователя с ID={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userOpt.get());
    }

    private void updateUserFields(User existingUser, User updatedUser) {
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getLogin() != null) {
            existingUser.setLogin(updatedUser.getLogin());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getBirthday() != null) {
            existingUser.setBirthday(updatedUser.getBirthday());
        }
    }
}