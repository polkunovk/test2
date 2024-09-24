package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {

    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    // Как я понимаю, ошибка в тестах на гитхаб происходит из-за длины в 200 символов. Не знаю пока, как это исправить, но сделаю.
    @Size(max = 200, message = "Описание фильма не может превышать 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза не может быть null.")
    @PastOrPresent(message = "Дата релиза не может быть в будущем.")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    private int duration;
}
