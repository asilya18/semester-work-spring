package ru.itis.service;

import ru.itis.dto.KinopoiskFilmDto;

import java.util.List;

public interface KinopoiskService {
    List<KinopoiskFilmDto> search(String query);
    KinopoiskFilmDto findById(String kinopoiskId);
}