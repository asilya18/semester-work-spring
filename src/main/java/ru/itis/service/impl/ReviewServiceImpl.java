package ru.itis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.ReviewForm;
import ru.itis.exception.ForbiddenException;
import ru.itis.exception.NotFoundException;
import ru.itis.model.MovieNight;
import ru.itis.model.Review;
import ru.itis.model.User;
import ru.itis.model.enums.MovieNightStatus;
import ru.itis.repository.MovieNightRepository;
import ru.itis.repository.ReviewRepository;
import ru.itis.service.ReviewService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieNightRepository movieNightRepository;

    @Override
    @Transactional
    public Review create(ReviewForm form, Long movieNightId, User author) {
        MovieNight movieNight = movieNightRepository.findById(movieNightId)
                .orElseThrow(() -> new NotFoundException("киновечер не найден"));
        if (movieNight.getStatus() != MovieNightStatus.FINISHED) {
            log.error("user {} tried to review movienight {} which is not finished", author.getId(), movieNightId);
            throw new ForbiddenException("отзывы можно оставлять только после завершения киновечера");
        }

        Review review = new Review();
        review.setMovieRating(form.getMovieRating());
        review.setEventRating(form.getEventRating());
        review.setComment(form.getComment());
        review.setMovieNight(movieNight);
        review.setAuthor(author);
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> findByMovieNight(MovieNight movieNight) {
        return reviewRepository.findByMovieNight(movieNight);
    }
}