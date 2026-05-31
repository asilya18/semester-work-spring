package ru.itis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itis.exception.AlreadyNominatedException;
import ru.itis.exception.ForbiddenException;
import ru.itis.model.Movie;
import ru.itis.security.UserPrincipal;
import ru.itis.service.MovieService;
import ru.itis.service.NominationService;

@Controller
@RequiredArgsConstructor
public class NominationController {
    private final NominationService nominationService;
    private final MovieService movieService;

    @PostMapping("/movienights/{movieNightId}/nominations")
    public String nominate(@PathVariable Long movieNightId,
                           @RequestParam(required = false) Long movieId,
                           @RequestParam(required = false) String kinopoiskId,
                           @AuthenticationPrincipal UserPrincipal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            Long resolvedMovieId = movieId; // кладем итоговый id
            if (resolvedMovieId == null && kinopoiskId != null) {
                Movie movie = movieService.saveFromKinopoisk(kinopoiskId);
                resolvedMovieId = movie.getId();
            }
            if (resolvedMovieId == null) {
                redirectAttributes.addFlashAttribute("error", "фильм не указан");
                return "redirect:/movienights/" + movieNightId;
            }
            nominationService.nominate(movieNightId, resolvedMovieId, principal.getUser());
        } catch (AlreadyNominatedException | ForbiddenException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movienights/" + movieNightId;
    }

    @PostMapping("/nominations/{nominationId}/delete")
    public String remove(@PathVariable Long nominationId,
                         @RequestParam Long movieNightId, // передаем скрыто в шаблоне, нужен только для редиректа
                         @AuthenticationPrincipal UserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            nominationService.remove(nominationId, principal.getUser());
        } catch (ForbiddenException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movienights/" + movieNightId;
    }

    @PostMapping("/nominations/{nominationId}/winner")
    public String setWinner(@PathVariable Long nominationId,
                            @RequestParam Long movieNightId,
                            @AuthenticationPrincipal UserPrincipal principal,
                            RedirectAttributes redirectAttributes) {
        try {
            nominationService.setWinner(nominationId, principal.getUser());
        } catch (ForbiddenException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movienights/" + movieNightId;
    }
}