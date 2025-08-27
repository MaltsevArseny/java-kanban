package ru.yandex.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.filmorate.model.User;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(nextId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}