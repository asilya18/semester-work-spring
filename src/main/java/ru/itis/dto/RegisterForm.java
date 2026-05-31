package ru.itis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {
    @NotBlank(message = "имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "имя пользователя от 3 до 50 символов")
    private String username;
    @NotBlank(message = "email обязателен")
    @Email(message = "некорректный email")
    private String email;
    @NotBlank(message = "пароль обязателен")
    @Size(min = 6, message = "пароль минимум 6 символов")
    private String password;
}