package ru.itis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KinopoiskSearchResponseDto {
    private List<KinopoiskFilmDto> films;
    //  "keyword": "интерстеллар",
    //    "films": [ {...}, {...}, {...} ]
    // а нам нужен только films
}