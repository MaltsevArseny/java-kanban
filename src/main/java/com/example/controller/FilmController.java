package com.example.controller;

import com.example.model.Film;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable Long id, @Valid @RequestBody Film updatedFilm) {
        Optional<Film> existingFilmOpt = films.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();

        if (existingFilmOpt.isEmpty()) {
            log.warn("Попытка обновления несуществующего фильма с ID={}", id);
            return ResponseEntity.notFound().build();
        }

        Film existingFilm = existingFilmOpt.get();
        updateFilmFields(existingFilm, updatedFilm);

        log.info("Обновлен фильм: ID={}, Название={}", existingFilm.getId(), existingFilm.getName());
        return ResponseEntity.ok(existingFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Запрошен список всех фильмов (количество: {})", films.size());
        return ResponseEntity.ok(new ArrayList<>(films));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        Optional<Film> filmOpt = films.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();

        if (filmOpt.isEmpty()) {
            log.warn("Запрос несуществующего фильма с ID={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(filmOpt.get());
    }

    private void updateFilmFields(Film existingFilm, Film updatedFilm) {
        if (updatedFilm.getName() != null) {
            existingFilm.setName(updatedFilm.getName());
        }
        if (updatedFilm.getDescription() != null) {
            existingFilm.setDescription(updatedFilm.getDescription());
        }
        if (updatedFilm.getReleaseDate() != null) {
            existingFilm.setReleaseDate(updatedFilm.getReleaseDate());
        }
        if (updatedFilm.getDuration() != null) {
            existingFilm.setDuration(updatedFilm.getDuration());
        }
    }
}