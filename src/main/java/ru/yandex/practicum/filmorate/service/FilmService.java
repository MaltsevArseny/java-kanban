package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Фильм с id " + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<MpaRating> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    public MpaRating findMpaById(int id) {
        return filmStorage.findMpaById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рейтинг MPA с id " + id + " не найден"));
    }

    public List<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(int id) {
        return filmStorage.findGenreById(id)
                .orElseThrow(() -> new IllegalArgumentException("Жанр с id " + id + " не найден"));
    }
}