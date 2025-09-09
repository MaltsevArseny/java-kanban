package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@SuppressWarnings("unused")
public class MpaController {
    private final FilmService filmService;

    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<MpaRating> findAll() {
        return filmService.findAllMpa();
    }

    @GetMapping("/{id}")
    public MpaRating findById(@PathVariable int id) {
        return filmService.findMpaById(id);
    }
}