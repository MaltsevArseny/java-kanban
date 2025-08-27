package ru.yandex.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.storage.film.FilmStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public Collection<Film> findAll() {
        log.debug("Получение всех фильмов из хранилища");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        log.debug("Создание фильма в хранилище: {}", film);
        Film createdFilm = filmStorage.create(film);
        log.debug("Фильм создан в хранилище: {}", createdFilm);
        return createdFilm;
    }

    public Film update(Film film) {
        log.debug("Обновление фильма в хранилище: {}", film);
        Film updatedFilm = filmStorage.update(film);
        log.debug("Фильм обновлен в хранилище: {}", updatedFilm);
        return updatedFilm;
    }

    public Optional<Film> findById(Long id) {
        log.debug("Поиск фильма по ID: {}", id);
        return filmStorage.findById(id);
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Добавление лайка фильму ID: {} пользователем ID: {}", filmId, userId);
        Film film = getFilmOrThrow(filmId);
        film.getLikes().add(userId);
        log.debug("Лайк добавлен. Теперь у фильма ID: {} {} лайков", filmId, film.getLikes().size());
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Удаление лайка фильму ID: {} пользователем ID: {}", filmId, userId);
        Film film = getFilmOrThrow(filmId);
        film.getLikes().remove(userId);
        log.debug("Лайк удален. Теперь у фильма ID: {} {} лайков", filmId, film.getLikes().size());
    }

    public List<Film> getPopularFilms(Integer count) {
        log.debug("Получение {} популярных фильмов", count);
        int resultCount = (count == null || count <= 0) ? 10 : count;

        List<Film> popularFilms = filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(resultCount)
                .collect(Collectors.toList());

        log.debug("Найдено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }

    private Film getFilmOrThrow(Long filmId) {
        return filmStorage.findById(filmId).orElseThrow(() -> {
            log.error("Фильм с id={} не найден", filmId);
            return new NoSuchElementException("Фильм с id=" + filmId + " не найден.");
        });
    }
}