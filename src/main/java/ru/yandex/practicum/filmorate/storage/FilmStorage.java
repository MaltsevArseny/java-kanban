package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);
    Film update(Film film);
    List<Film> findAll();
    Optional<Film> findById(Long id);
    void addLike(Long filmId, Long userId);
    void removeLike(Long filmId, Long userId);

    List<MpaRating> findAllMpa();
    Optional<MpaRating> findMpaById(int id);

    List<Genre> findAllGenres();
    Optional<Genre> findGenreById(int id);
}