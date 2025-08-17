package com.example.controller;

import com.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/users")
@Slf4j
@SuppressWarnings("unused") // Добавляем аннотацию для всего класса
public class UserController {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    @PostMapping
    @SuppressWarnings("unused") // Для метода createUser
    public User createUser(@RequestBody User user) {
        user.setId(counter.incrementAndGet());
        users.add(user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @GetMapping
    @SuppressWarnings("unused") // Для метода getAllUsers
    public List<User> getAllUsers() {
        log.info("Получен запрос всех пользователей");
        return users;
    }
}