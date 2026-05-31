package ru.itis.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieForm {
    @NotBlank(message = "название обязательно")
    private String title;
    @Min(value = 1888, message = "год не может быть раньше 1888")
    @Max(value = 2026, message = "некорректный год")
    private Integer year;
    private String description;
    private String posterUrl;
    private Integer durationMinutes;
}