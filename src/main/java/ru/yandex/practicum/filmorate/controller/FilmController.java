package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final List<Film> films = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(currentId++);
        films.add(film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() <= 0 || film.getId() > currentId - 1) { // Use currentId to check valid ID range
            log.warn("Попытка обновления фильма с ID: {}", film.getId());
            throw new ValidationException("Фильм с таким ID не найден.");
        }
        films.set(film.getId() - 1, film);
        log.info("Фильм обновлен: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }
}
