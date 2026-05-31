package ru.itis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class MovieSearchResultDto implements Serializable {
    private Long id; // null если фильм ещё не в нашей бд
    private String title;
    private Integer year;
    private String posterUrl;
    private String kinopoiskId; // для js
    private String source;
}