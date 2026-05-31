package ru.itis.repository;

import ru.itis.model.Movie;

import java.util.List;

public interface MovieRepositoryCustom {
    List<Movie> searchMovies(String title, Integer year);
    // этот интерфейс мы реализуем отдельно, потому что
    // criteriaBuilder требует использования entityManager
}