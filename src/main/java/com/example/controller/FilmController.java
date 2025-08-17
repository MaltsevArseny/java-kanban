package com.example.controller;

import com.example.model.Film;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        film.setId(counter.incrementAndGet());
        films.add(film);
        log.info("Добавлен фильм: ID={}, Название={}", film.getId(), film.getName());
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Запрошен список всех фильмов (количество: {})", films.size());
        return ResponseEntity.ok(new ArrayList<>(films));
    }
}