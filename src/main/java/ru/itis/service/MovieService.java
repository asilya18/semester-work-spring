package ru.itis.service;

import ru.itis.dto.MovieForm;
import ru.itis.dto.MovieSearchResultDto;
import ru.itis.model.Movie;

import java.util.List;

public interface MovieService {
    Movie create(MovieForm form);
    Movie findById(Long id);
    List<Movie> findAll();
    List<Movie> findManuallyAdded();
    Movie update(Long id, MovieForm form);
    void delete(Long id);
    // поиск по бд и кинопоиску одновременно
    List<MovieSearchResultDto> search(String query);
    Movie saveFromKinopoisk(String kinopoiskId);
}