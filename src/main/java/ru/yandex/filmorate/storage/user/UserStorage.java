package ru.yandex.filmorate.storage.user;

import ru.yandex.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();
    User create(User user);
    User update(User user);
    Optional<User> findById(Long id);
    void delete(Long id);

}