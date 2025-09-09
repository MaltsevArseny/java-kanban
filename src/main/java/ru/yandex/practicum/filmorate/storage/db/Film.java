package ru.yandex.practicum.filmorate.storage.db;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@SuppressWarnings("unused")
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaRating mpa;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();

    public Film() {
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, MpaRating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public static FilmBuilder builder() {
        return new FilmBuilder();
    }

    public static class FilmBuilder {
        private Long id;
        private String name;
        private String description;
        private LocalDate releaseDate;
        private int duration;
        private MpaRating mpa;
        private Set<Long> likes = new HashSet<>();
        private Set<Genre> genres = new HashSet<>();

        public FilmBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FilmBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FilmBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FilmBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public FilmBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public FilmBuilder mpa(MpaRating mpa) {
            this.mpa = mpa;
            return this;
        }

        public FilmBuilder likes(Set<Long> likes) {
            this.likes = likes;
            return this;
        }

        public FilmBuilder genres(Set<Genre> genres) {
            this.genres = genres;
            return this;
        }

        public Film build() {
            Film film = new Film(id, name, description, releaseDate, duration, mpa);
            film.setLikes(likes);
            film.setGenres(genres);
            return film;
        }
    }
}