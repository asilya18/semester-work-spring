package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.model.MovieNight;
import ru.itis.model.Participant;
import ru.itis.model.User;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByUserAndMovieNight(User user, MovieNight movieNight);

    List<Participant> findByMovieNight(MovieNight movieNight);

    boolean existsByUserAndMovieNight(User user, MovieNight movieNight);
}