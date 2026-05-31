package ru.itis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KinopoiskFilmDto {
    // парсинг json от кинопоиска
    // игнорирует лишние поля по типу rating
    private Long filmId;
    private String nameRu;
    private String nameEn;
    private String year;
    private String posterUrl;
    private String filmLength; // часы:минуты
    private String description;
}