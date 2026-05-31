package ru.itis.service;

import ru.itis.model.MovieNight;
import ru.itis.model.Nomination;
import ru.itis.model.User;

import java.util.List;
import java.util.Set;

public interface NominationService {
    Nomination nominate(Long movieNightId, Long movieId, User user);
    void remove(Long nominationId, User user);
    void setWinner(Long nominationId, User currentUser);
    List<Nomination> findByMovieNight(MovieNight movieNight);
    Nomination findById(Long id);
    Set<Long> findNotVotedIds(MovieNight movieNight, User user);
}