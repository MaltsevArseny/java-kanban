package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@SuppressWarnings("unused") // Подавляет предупреждения о неиспользуемых конструкторах и методах
public class Genre {
    private int id;
    private String name;

    // Конструкторы
    public Genre() {
        // Используется Spring и Jackson для создания объектов через рефлексию
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
        // Может использоваться в тестах или других частях приложения
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}