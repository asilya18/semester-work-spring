package ru.itis.service;

import ru.itis.model.MovieNight;
import ru.itis.model.User;

public interface ParticipantService {
    void join(Long movieNightId, User user);
    void leave(Long movieNightId, User user);
    boolean isParticipant(User user, MovieNight movieNight);
}