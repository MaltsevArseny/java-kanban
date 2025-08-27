package ru.yandex.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.storage.film.FilmStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Optional<Film> findById(Long id) {
        return filmStorage.findById(id);
    }

    // Новая бизнес-логика: Управление лайками
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        film.getLikes().add(userId); // Set обеспечивает уникальность
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        int resultCount = (count == null || count <= 0) ? 10 : count;

        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size())) // Сортируем по убыванию лайков
                .limit(resultCount)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(Long filmId) {
        return filmStorage.findById(filmId).orElseThrow(() -> new NoSuchElementException("Фильм с id=" + filmId + " не найден."));
    }
}