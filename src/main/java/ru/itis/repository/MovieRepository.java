package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {
    Optional<Movie> findByKinopoiskId(String kinopoiskId);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByKinopoiskIdIsNull();
}