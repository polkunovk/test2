package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController; // Внедрение FilmController
	@Autowired
	private UserController userController; // Внедрение UserController

	private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private final Validator validator = factory.getValidator();

	private Film film;
	private User user;

	@BeforeEach
	void setUp() {
		film = new Film();
		user = new User();
	}

	@Test
	void filmValidationTest() {
		// Проверка на пустое название
		film.setName("");
		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Название фильма должно быть заполнено.");

		// Проверка на превышение длины описания
		film.setName("Название");
		film.setDescription("A".repeat(201));
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Описание фильма не может превышать 200 символов.");

		// Проверка на будущее дату релиза
		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.of(3000, 1, 1));
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Дата релиза не должна быть в будущем.");

		// Проверка на отрицательную продолжительность
		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(-10);
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом.");

		// Проверка на валидный фильм
		film.setDuration(120);
		violations = validator.validate(film);
		assertTrue(violations.isEmpty(), "Фильм должен быть валидным.");

		// Проверка на валидный случай
		film.setName("Valid Film");
		film.setDescription("Valid Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);
		violations = validator.validate(film);
		assertTrue(violations.isEmpty(), "Фильм должен быть валидным.");

		// Проверка на нулевую продолжительность
		film.setDuration(0);
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом.");
	}

	@Test
	void userValidationTest() {
		// Проверка на пустую электронную почту
		user.setEmail("");
		user.setLogin("validLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Электронная почта не может быть пустой.");

		// Проверка на неверный формат электронной почты
		user.setEmail("invalid");
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Электронная почта должна быть валидной.");

		// Проверка на пустой логин
		user.setEmail("user@user.com");
		user.setLogin("");
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Логин должен быть заполнен.");

		// Проверка на пробелы в логине
		user.setLogin("invalid login");
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Логин не должен содержать пробелы.");

		// Проверка на дату рождения в будущем
		user.setLogin("validLogin");
		user.setBirthday(LocalDate.of(3000, 1, 1));
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Дата рождения не может быть в будущем.");

		// Проверка на валидного пользователя
		user.setBirthday(LocalDate.of(2000, 1, 1));
		violations = validator.validate(user);
		assertTrue(violations.isEmpty(), "Пользователь должен быть валидным.");

		// Проверка, что если имя отображения не задано, используется логин
		user.setName(null);
		assertEquals("validLogin", user.getName(), "Имя отображения должно быть логином, если не задано.");

		// Проверка, что имя отображения может быть задано
		user.setName("Custom Name");
		assertEquals("Custom Name", user.getName(), "Имя отображения должно быть заданным именем.");
	}

	@Test
	void userUpdateValidationTest() {
		user.setId(100); // Установите несуществующий ID
		Exception exception = assertThrows(ValidationException.class, () -> {
			userController.updateUser(user); // Вызов метода обновления пользователя
		});
		assertEquals("Пользователь с таким ID не найден.", exception.getMessage());
	}

	@Test
	void filmUpdateValidationTest() {
		film.setId(100); // Установите несуществующий ID
		Exception exception = assertThrows(ValidationException.class, () -> {
			filmController.updateFilm(film); // Вызов метода обновления фильма
		});
		assertEquals("Фильм с таким ID не найден.", exception.getMessage());
	}
}
