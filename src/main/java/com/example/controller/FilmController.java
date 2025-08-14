package com.example.controller;

import com.example.model.Film;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/films")
@Slf4j
@SuppressWarnings("unused")
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    @PostMapping
    @SuppressWarnings("unused")
    public Film addFilm(@RequestBody Film film) {
        film.setId(counter.incrementAndGet());
        films.add(film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @GetMapping
    @SuppressWarnings("unused")
    public List<Film> getAllFilms() {
        log.info("Получен запрос всех фильмов");
        return films;
    }
}