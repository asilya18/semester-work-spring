package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.model.MovieNight;
import ru.itis.model.Review;
import ru.itis.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieNight(MovieNight movieNight);

    List<Review> findByAuthor(User author);

    Optional<Review> findByMovieNightAndAuthor(MovieNight movieNight, User author);
}