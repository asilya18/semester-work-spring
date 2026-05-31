package ru.itis.service;

import ru.itis.dto.MovieNightForm;
import ru.itis.model.MovieNight;
import ru.itis.model.User;
import java.util.List;

public interface MovieNightService {
    MovieNight create(MovieNightForm form, User creator);
    MovieNight findById(Long id);
    List<MovieNight> findAll();
    List<MovieNight> findByCreator(User creator);
    List<MovieNight> findByParticipant(User user);
    MovieNight update(Long id, MovieNightForm form, User currentUser);
    void delete(Long id, User currentUser);
}