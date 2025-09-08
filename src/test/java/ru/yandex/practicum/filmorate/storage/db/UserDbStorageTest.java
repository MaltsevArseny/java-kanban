package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
@SuppressWarnings("unused") // Подавляет предупреждения о неиспользуемых полях
class UserDbStorageTest {
    @Autowired
    private UserDbStorage userStorage;

    @Test
    void shouldCreateAndFindUserById() {
        User user = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userStorage.create(user);
        assertThat(createdUser).isNotNull();

        Optional<User> foundUser = userStorage.findById(createdUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        User user = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userStorage.create(user);
        createdUser.setName("Updated Name");

        User updatedUser = userStorage.update(createdUser);
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }
}