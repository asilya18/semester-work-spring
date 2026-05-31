package ru.itis.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.MovieForm;
import ru.itis.dto.MovieSearchResultDto;
import ru.itis.model.Movie;
import ru.itis.service.MovieService;

import java.util.List;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public String list(@RequestParam(required = false) String filter, Model model) {
        model.addAttribute("movies",
                "manual".equals(filter) ? movieService.findManuallyAdded() : movieService.findAll());
        model.addAttribute("filter", filter);
        return "movies/list";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) Long movieNightId, Model model) {
        List<MovieSearchResultDto> all = movieService.findAll().stream()
                .map(m -> new MovieSearchResultDto(
                        m.getId(), m.getTitle(), m.getYear(), m.getPosterUrl(), m.getKinopoiskId(), "DB"))
                .toList();
        model.addAttribute("results", all);
        model.addAttribute("movieNightId", movieNightId);
        return "movies/search";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new MovieForm());
        return "movies/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") MovieForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "movies/form";
        }
        Movie movie = movieService.create(form);
        return "redirect:/movies/" + movie.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("movie", movieService.findById(id));
        return "movies/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id);
        MovieForm form = new MovieForm();
        form.setTitle(movie.getTitle());
        form.setYear(movie.getYear());
        form.setDescription(movie.getDescription());
        form.setPosterUrl(movie.getPosterUrl());
        form.setDurationMinutes(movie.getDurationMinutes());
        model.addAttribute("form", form);
        model.addAttribute("movieId", id);
        return "movies/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") MovieForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "movies/form";
        }
        movieService.update(id, form);
        return "redirect:/movies/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        movieService.delete(id);
        return "redirect:/movies";
    }

    @GetMapping("/kinopoisk/{kinopoiskId}")
    public String fromKinopoisk(@PathVariable String kinopoiskId) {
        Movie movie = movieService.saveFromKinopoisk(kinopoiskId);
        return "redirect:/movies/" + movie.getId();
    }
}