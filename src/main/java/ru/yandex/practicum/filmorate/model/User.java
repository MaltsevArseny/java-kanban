package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@SuppressWarnings("unused") // Подавляет предупреждения о неиспользуемых конструкторах и методах
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    // Конструкторы
    public User() {
        // Используется Spring и Jackson для создания объектов через рефлексию
    }

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        // Может использоваться в тестах или других частях приложения
    }

    // Метод builder для совместимости с существующим кодом
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Вложенный класс UserBuilder
    public static class UserBuilder {
        private Long id;
        private String email;
        private String login;
        private String name;
        private LocalDate birthday;
        private Set<Long> friends = new HashSet<>();

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder login(String login) {
            this.login = login;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder birthday(LocalDate birthday) {
            this.birthday = birthday;
            return this;
        }

        public UserBuilder friends(Set<Long> friends) {
            this.friends = friends;
            return this;
            // Может использоваться для установки начального списка друзей
        }

        public User build() {
            User user = new User(id, email, login, name, birthday);
            user.setFriends(friends);
            return user;
        }
    }
}