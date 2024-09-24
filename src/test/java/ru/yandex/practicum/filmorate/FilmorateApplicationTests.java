package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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

	private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private final Validator validator = factory.getValidator();

	@Test
	void filmValidationTest() {
		Film film = new Film();

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
	}

	@Test
	void userValidationTest() {
		User user = new User();

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
	}
}