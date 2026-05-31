package ru.itis.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itis.dto.MovieNightForm;
import ru.itis.dto.ReviewForm;
import ru.itis.exception.AlreadyJoinedException;
import ru.itis.exception.ForbiddenException;
import ru.itis.model.MovieNight;
import ru.itis.model.Nomination;
import ru.itis.model.User;
import ru.itis.security.UserPrincipal;
import ru.itis.converter.MovieNightToFormConverter;
import ru.itis.service.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movienights")
@RequiredArgsConstructor
@Slf4j
public class MovieNightController {
    private final MovieNightService movieNightService;
    private final ParticipantService participantService;
    private final NominationService nominationService;
    private final ReviewService reviewService;
    private final VoteService voteService;
    private final MovieNightToFormConverter movieNightToFormConverter;

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserPrincipal principal) {
        model.addAttribute("movieNights", movieNightService.findAll());
        model.addAttribute("currentUser", principal.getUser());
        return "movienights/list";
    }

    @GetMapping("/my")
    public String my(Model model, @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = principal.getUser();
        model.addAttribute("created", movieNightService.findByCreator(currentUser));
        model.addAttribute("participating", movieNightService.findByParticipant(currentUser));
        model.addAttribute("currentUser", currentUser);
        return "movienights/my";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         Model model,
                         @AuthenticationPrincipal UserPrincipal principal) {
        MovieNight movieNight = movieNightService.findById(id);
        User currentUser = principal.getUser();
        List<Nomination> nominations = nominationService.findByMovieNight(movieNight);
        Set<Long> notVotedIds = nominationService.findNotVotedIds(movieNight, currentUser);
        Map<Long, Boolean> hasVotedMap = nominations.stream()
                .collect(Collectors.toMap(
                        Nomination::getId,
                        n -> !notVotedIds.contains(n.getId())
                ));

        Map<Long, Long> voteCountMap = nominations.stream()
                .collect(Collectors.toMap(
                        Nomination::getId,
                        n -> voteService.countVotes(n)
                ));
        model.addAttribute("movieNight", movieNight);
        model.addAttribute("nominations", nominations);
        model.addAttribute("reviews", reviewService.findByMovieNight(movieNight));
        model.addAttribute("isParticipant", participantService.isParticipant(currentUser, movieNight));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("hasVotedMap", hasVotedMap);
        model.addAttribute("voteCountMap", voteCountMap);
        model.addAttribute("reviewForm", new ReviewForm());
        return "movienights/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new MovieNightForm());
        return "movienights/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") MovieNightForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserPrincipal principal) {
        if (bindingResult.hasErrors()) return "movienights/form";
        MovieNight movieNight = movieNightService.create(form, principal.getUser());
        return "redirect:/movienights/" + movieNight.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        MovieNight movieNight = movieNightService.findById(id);
        model.addAttribute("form", movieNightToFormConverter.convert(movieNight));
        model.addAttribute("movieNightId", id);
        return "movienights/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") MovieNightForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "movienights/form";
        try {
            movieNightService.update(id, form, principal.getUser());
        } catch (ForbiddenException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movienights/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            movieNightService.delete(id, principal.getUser());
            return "redirect:/movienights";
        } catch (ForbiddenException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/movienights/" + id;
        }
    }

    @PostMapping("/{id}/join")
    public String join(@PathVariable Long id,
                       @AuthenticationPrincipal UserPrincipal principal,
                       RedirectAttributes redirectAttributes) {
        try {
            participantService.join(id, principal.getUser());
        } catch (AlreadyJoinedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movienights/" + id;
    }

    @PostMapping("/{id}/leave")
    public String leave(@PathVariable Long id,
                        @AuthenticationPrincipal UserPrincipal principal,
                        RedirectAttributes redirectAttributes) {
        try {
            participantService.leave(id, principal.getUser());
        } catch (ForbiddenException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movienights/" + id;
    }

}