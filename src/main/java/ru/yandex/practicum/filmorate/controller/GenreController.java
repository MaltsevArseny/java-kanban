package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;

@RestController
@RequestMapping("/genres")
@SuppressWarnings("unused")
public class GenreController {
    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genre> findAll() {
        return filmService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable int id) {
        return filmService.findGenreById(id);
    }
}