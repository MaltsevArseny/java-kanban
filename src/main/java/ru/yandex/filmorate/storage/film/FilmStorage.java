package ru.yandex.filmorate.storage.film;

import ru.yandex.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();
    Film create(Film film);
    Film update(Film film);
    Optional<Film> findById(Long id);

    void delete(Long id);

}