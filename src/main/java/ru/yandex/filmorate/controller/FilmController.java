package ru.yandex.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.service.FilmService;
import jakarta.validation.Valid;
import java.util.*;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Создание фильма: {}", film);
        Film createdFilm = filmService.create(film);
        log.info("Фильм создан успешно: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма: {}", film);
        Film updatedFilm = filmService.update(film);
        log.info("Фильм обновлен успешно: {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        log.info("Получение фильма по ID: {}", id);
        Film film = filmService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с id=" + id + " не найден."));
        log.info("Фильм найден: {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление лайка фильму ID: {} от пользователя ID: {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Лайк добавлен успешно");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление лайка фильму ID: {} от пользователя ID: {}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Лайк удален успешно");
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получение популярных фильмов, количество: {}", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Найдено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}