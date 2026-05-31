package ru.itis.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.ErrorResponse;
import ru.itis.dto.MovieForm;
import ru.itis.dto.MovieSearchResultDto;
import ru.itis.exception.NotFoundException;
import ru.itis.model.Movie;
import ru.itis.service.MovieService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "movies", description = "фильмы")
public class MovieRestController {
    private final MovieService movieService;

    @GetMapping("/search")
    @Operation(summary = "поиск фильмов по названию (БД + Кинопоиск)")
    public ResponseEntity<List<MovieSearchResultDto>> search(@RequestParam String query) {
        return ResponseEntity.ok(movieService.search(query));
    }

    @GetMapping("/manual")
    @Operation(summary = "фильмы добавленные вручную (без Кинопоиска) — только для админа")
    public ResponseEntity<List<MovieSearchResultDto>> manuallyAdded() {
        List<MovieSearchResultDto> result = movieService.findManuallyAdded().stream()
                .map(m -> new MovieSearchResultDto(m.getId(), m.getTitle(), m.getYear(), m.getPosterUrl(), null, "DB"))
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "добавить фильм вручную — только для админа")
    public ResponseEntity<?> create(@Valid @RequestBody MovieForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(validationErrors(result));
        }
        Movie movie = movieService.create(form);
        return ResponseEntity.ok(toDto(movie));
    }

    @PutMapping("/{id}")
    @Operation(summary = "обновить фильм — только для админа")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody MovieForm form,
                                    BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(validationErrors(result));
        }
        try {
            Movie movie = movieService.update(id, form);
            return ResponseEntity.ok(toDto(movie));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "удалить фильм — только для админа")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            movieService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> validationErrors(BindingResult result) {
        // формирует json  с ошибками валидации
        return Map.of("errors", result.getFieldErrors().stream()
                .collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage(), (a, b) -> a)));
    }

    private MovieSearchResultDto toDto(Movie m) {
        return new MovieSearchResultDto(m.getId(), m.getTitle(), m.getYear(), m.getPosterUrl(), m.getKinopoiskId(), "DB");
    }
}