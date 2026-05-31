package ru.itis.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itis.dto.ErrorResponse;
import ru.itis.exception.AlreadyVotedException;
import ru.itis.exception.ForbiddenException;
import ru.itis.exception.NotFoundException;
import ru.itis.model.Nomination;
import ru.itis.security.UserPrincipal;
import ru.itis.service.NominationService;
import ru.itis.service.VoteService;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Tag(name = "votes", description = "голосование за номинации")
public class VoteRestController {
    private final VoteService voteService;
    private final NominationService nominationService;

    @PostMapping("/{nominationId}")
    @Operation(summary = "проголосовать за номинацию")
    public ResponseEntity<?> vote(@PathVariable Long nominationId,
                                  @AuthenticationPrincipal UserPrincipal principal) {
        try {
            Nomination nomination = nominationService.findById(nominationId);
            voteService.vote(nominationId, principal.getUser());
            long count = voteService.countVotes(nomination);
            return ResponseEntity.ok(Map.of("success", true, "voteCount", count));
        } catch (AlreadyVotedException | ForbiddenException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{nominationId}")
    @Operation(summary = "убрать голос с номинации")
    public ResponseEntity<?> unvote(@PathVariable Long nominationId,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        try {
            Nomination nomination = nominationService.findById(nominationId);
            voteService.unvote(nominationId, principal.getUser());
            long count = voteService.countVotes(nomination);
            return ResponseEntity.ok(Map.of("success", true, "voteCount", count));
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}