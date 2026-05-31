package ru.itis.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itis.dto.ReviewForm;
import ru.itis.exception.ForbiddenException;
import ru.itis.security.UserPrincipal;
import ru.itis.service.ReviewService;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/movienights/{movieNightId}/reviews")
    public String create(@PathVariable Long movieNightId,
                         @Valid @ModelAttribute("reviewForm") ReviewForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("reviewError", "проверьте правильность введённых данных");
            return "redirect:/movienights/" + movieNightId;
            // редиректим и теряем введенные данные, тк иначе нужна полная модель movienights/detail
        }
        try {
            reviewService.create(form, movieNightId, principal.getUser());
        } catch (ForbiddenException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movienights/" + movieNightId;
    }
}