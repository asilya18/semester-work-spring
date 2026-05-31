package ru.itis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.KinopoiskFilmDto;
import ru.itis.dto.MovieForm;
import ru.itis.dto.MovieSearchResultDto;
import ru.itis.exception.NotFoundException;
import ru.itis.model.Movie;
import ru.itis.repository.MovieRepository;
import ru.itis.service.KinopoiskService;
import ru.itis.service.MovieService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final KinopoiskService kinopoiskService;

    @Override
    @Transactional
    @CacheEvict(value = "movie-search", allEntries = true)
    //  allEntries = true - сбрасываем весь кэш поиска целиком
    public Movie create(MovieForm form) {
        Movie movie = new Movie();
        movie.setTitle(form.getTitle());
        movie.setYear(form.getYear());
        movie.setDescription(form.getDescription());
        movie.setPosterUrl(form.getPosterUrl());
        movie.setDurationMinutes(form.getDurationMinutes());
        return movieRepository.save(movie);
    }

    @Override
    @Cacheable(value = "movies", key = "#id")
    public Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("фильм не найден"));
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> findManuallyAdded() {
        return movieRepository.findByKinopoiskIdIsNull();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"movies", "movie-search"}, allEntries = true)
    public Movie update(Long id, MovieForm form) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("фильм не найден"));
        movie.setTitle(form.getTitle());
        movie.setYear(form.getYear());
        movie.setDescription(form.getDescription());
        movie.setPosterUrl(form.getPosterUrl());
        movie.setDurationMinutes(form.getDurationMinutes());
        return movieRepository.save(movie);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"movies", "movie-search"}, allEntries = true)
    public void delete(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("фильм не найден"));
        movieRepository.delete(movie);
    }

    @Override
    @Cacheable(value = "movie-search", key = "#query")
    public List<MovieSearchResultDto> search(String query) {
        List<MovieSearchResultDto> results = new ArrayList<>();
        List<Movie> dbMovies = movieRepository.searchMovies(query, null);
        Set<String> dbKinopoiskIds = dbMovies.stream()
                .map(Movie::getKinopoiskId)
                .filter(id -> id != null)
                .collect(Collectors.toSet()); // берем из бд фильмы которые были добавлены из кинопоиска
        // чтобы потом не было дубликатов
        dbMovies.forEach(m -> results.add(
                new MovieSearchResultDto(m.getId(), m.getTitle(), m.getYear(), m.getPosterUrl(), m.getKinopoiskId(), "DB")
        ));

        try {
            List<KinopoiskFilmDto> kinopoiskMovies = kinopoiskService.search(query);
            kinopoiskMovies.stream()
                    .filter(k -> !dbKinopoiskIds.contains(String.valueOf(k.getFilmId())))
                    .forEach(k -> results.add(new MovieSearchResultDto(
                            null, // у фильма пока нет id нашей бд
                            k.getNameRu() != null ? k.getNameRu() : k.getNameEn(),
                            k.getYear() != null ? parseYear(k.getYear()) : null,
                            k.getPosterUrl(),
                            String.valueOf(k.getFilmId()),
                            "KINOPOISK"
                    )));
        } catch (Exception e) {
            log.error("error searching Kinopoisk: {}", e.getMessage(), e);
        }
        return results;
    }

    @Override
    @Transactional
    public Movie saveFromKinopoisk(String kinopoiskId) {
        return movieRepository.findByKinopoiskId(kinopoiskId).orElseGet(() -> {
            KinopoiskFilmDto dto = kinopoiskService.findById(kinopoiskId);
            Movie movie = new Movie();
            movie.setTitle(dto.getNameRu() != null ? dto.getNameRu() : dto.getNameEn());
            movie.setYear(dto.getYear() != null ? parseYear(dto.getYear()) : null);
            movie.setDescription(dto.getDescription());
            movie.setPosterUrl(dto.getPosterUrl());
            movie.setDurationMinutes(parseFilmLength(dto.getFilmLength()));
            movie.setKinopoiskId(kinopoiskId);
            return  movieRepository.save(movie);
        });
    }

    private Integer parseYear(String year) {
        try {
            return Integer.parseInt(year);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseFilmLength(String filmLength) {
        if (filmLength == null || filmLength.isBlank()) {
            return null;
        }
        String[] parts = filmLength.split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}