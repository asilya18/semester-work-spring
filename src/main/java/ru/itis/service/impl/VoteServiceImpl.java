package ru.itis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.exception.AlreadyVotedException;
import ru.itis.exception.ForbiddenException;
import ru.itis.exception.NotFoundException;
import ru.itis.model.Nomination;
import ru.itis.model.User;
import ru.itis.model.Vote;
import ru.itis.model.enums.MovieNightStatus;
import ru.itis.repository.NominationRepository;
import ru.itis.repository.ParticipantRepository;
import ru.itis.repository.VoteRepository;
import ru.itis.service.VoteService;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final NominationRepository nominationRepository;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional
    public Vote vote(Long nominationId, User user) {
        Nomination nomination = nominationRepository.findById(nominationId)
                .orElseThrow(() -> new NotFoundException("номинация не найдена"));
        if (nomination.getMovieNight().getStatus() != MovieNightStatus.PLANNED) {
            throw new ForbiddenException("голосование доступно только в статусе PLANNED");
        }
        if (!participantRepository.existsByUserAndMovieNight(user, nomination.getMovieNight())) {
            throw new ForbiddenException("только участники могут голосовать");
        }
        if (voteRepository.existsByNominationAndUser(nomination, user)) {
            log.error("user {} already voted for nomination {}", user.getId(), nominationId);
            throw new AlreadyVotedException("вы уже голосовали за этот фильм");
        }

        Vote vote = new Vote();
        vote.setNomination(nomination);
        vote.setUser(user);
        return voteRepository.save(vote);
    }

    @Override
    @Transactional
    public void unvote(Long nominationId, User user) {
        Nomination nomination = nominationRepository.findById(nominationId)
                .orElseThrow(() -> new NotFoundException("номинация не найдена"));
        Vote vote = voteRepository.findByNominationAndUser(nomination, user)
                .orElseThrow(() -> new NotFoundException("голос не найден"));
        voteRepository.delete(vote);
    }

    @Override
    public boolean hasVoted(Nomination nomination, User user) {
        return voteRepository.existsByNominationAndUser(nomination, user);
    }

    @Override
    public long countVotes(Nomination nomination) {
        return voteRepository.countByNomination(nomination);
    }
}