package ru.itis.service;

import ru.itis.dto.ReviewForm;
import ru.itis.model.MovieNight;
import ru.itis.model.Review;
import ru.itis.model.User;

import java.util.List;

public interface ReviewService {
    Review create(ReviewForm form, Long movieNightId, User author);
    List<Review> findByMovieNight(MovieNight movieNight);
}