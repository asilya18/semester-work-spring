package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.model.MovieNight;
import ru.itis.model.Nomination;
import ru.itis.model.User;

import java.util.List;
import java.util.Optional;

public interface NominationRepository extends JpaRepository<Nomination, Long> {
    List<Nomination> findByMovieNight(MovieNight movieNight);

    Optional<Nomination> findByMovieIdAndMovieNightId(Long movieId, Long movieNightId);

    boolean existsByNominatedByAndMovieNight(User nominatedBy, MovieNight movieNight);

    // номинации за которые юзер ещё не голосовал
    @Query("""
            select n from Nomination n
            where n.movieNight = :movieNight
            and n not in (
                select v.nomination from Vote v where v.user = :user
            )
            """)
    List<Nomination> findNotVotedByUser(@Param("movieNight") MovieNight movieNight,
                                        @Param("user") User user);
}