package ru.itis.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {
    @Min(value = 1, message = "минимальная оценка 1")
    @Max(value = 10, message = "максимальная оценка 10")
    private Integer movieRating;
    @Min(value = 1, message = "минимальная оценка 1")
    @Max(value = 10, message = "максимальная оценка 10")
    private Integer eventRating;
    private String comment;
}