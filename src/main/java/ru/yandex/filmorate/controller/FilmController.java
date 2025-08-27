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
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    // Новый endpoint для получения фильма по ID
    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с id=" + id + " не найден."));
    }

    // Новые endpoints для управления лайками
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}