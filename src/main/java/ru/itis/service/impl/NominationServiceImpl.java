package ru.itis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.exception.AlreadyNominatedException;
import ru.itis.exception.ForbiddenException;
import ru.itis.exception.NotFoundException;
import ru.itis.model.MovieNight;
import ru.itis.model.Nomination;
import ru.itis.model.User;
import ru.itis.model.enums.MovieNightStatus;
import ru.itis.model.enums.ParticipantRole;
import ru.itis.repository.MovieNightRepository;
import ru.itis.repository.MovieRepository;
import ru.itis.repository.NominationRepository;
import ru.itis.repository.ParticipantRepository;
import ru.itis.service.NominationService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NominationServiceImpl implements NominationService {
    private final NominationRepository nominationRepository;
    private final MovieNightRepository movieNightRepository;
    private final MovieRepository movieRepository;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional
    public Nomination nominate(Long movieNightId, Long movieId, User user) {
        MovieNight movieNight = movieNightRepository.findById(movieNightId)
                .orElseThrow(() -> new NotFoundException("киновечер не найден"));
        if (movieNight.getStatus() != MovieNightStatus.PLANNED) {
            throw new ForbiddenException("номинировать фильмы можно только в статусе PLANNED");
        }
        if (!participantRepository.existsByUserAndMovieNight(user, movieNight)) {
            throw new ForbiddenException("только участники могут номинировать фильмы");
        }
        if (nominationRepository.findByMovieNight(movieNight).size() >= 5) {
            throw new ForbiddenException("достигнут лимит номинаций (5 фильмов)");
        }
        if (nominationRepository.existsByNominatedByAndMovieNight(user, movieNight)) {
            throw new ForbiddenException("вы уже номинировали фильм на этот киновечер");
        }
        if (nominationRepository.findByMovieIdAndMovieNightId(movieId, movieNightId).isPresent()) {
            log.error("movie {} already nominated for movienight {}", movieId, movieNightId);
            throw new AlreadyNominatedException("этот фильм уже номинирован на данный киновечер");
        }

        Nomination nomination = new Nomination();
        nomination.setMovie(movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException("фильм не найден")));
        nomination.setMovieNight(movieNight);
        nomination.setNominatedBy(user);
        nomination.setWinner(false);
        return nominationRepository.save(nomination);
    }

    @Override
    @Transactional
    public void remove(Long nominationId, User user) {
        Nomination nomination = nominationRepository.findById(nominationId)
                .orElseThrow(() -> new NotFoundException("номинация не найдена"));
        boolean isNominator = nomination.getNominatedBy().equals(user);
        boolean isHost = participantRepository
                .findByUserAndMovieNight(user, nomination.getMovieNight())
                .map(p -> p.getRole() == ParticipantRole.HOST)
                .orElse(false);
        if (!isNominator && !isHost) {
            log.error("user {} tried to remove nomination {} without permission", user.getId(), nominationId);
            throw new ForbiddenException("только автор номинации или создатель киновечера может удалять номинацию");
        }
        nominationRepository.delete(nomination);
    }

    @Override
    @Transactional
    public void setWinner(Long nominationId, User currentUser) {
        Nomination nomination = nominationRepository.findById(nominationId)
                .orElseThrow(() -> new NotFoundException("номинация не найдена"));
        MovieNight movieNight = nomination.getMovieNight();
        if (!movieNight.getCreator().equals(currentUser)) {
            log.error("user {} tried to set winner without permission", currentUser.getId());
            throw new ForbiddenException("только создатель киновечера может назначить победителя");
        }
        // сбрасываем предыдущего победителя, если он есть
        nominationRepository.findByMovieNight(movieNight)
                .forEach(n -> {
                    n.setWinner(false);
                    nominationRepository.save(n);
                });
        nomination.setWinner(true);
        nominationRepository.save(nomination);
    }

    @Override
    public List<Nomination> findByMovieNight(MovieNight movieNight) {
        return nominationRepository.findByMovieNight(movieNight);
    }

    @Override
    public Nomination findById(Long id) {
        return nominationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("номинация не найдена"));
    }

    @Override
    public Set<Long> findNotVotedIds(MovieNight movieNight, User user) {
        return nominationRepository.findNotVotedByUser(movieNight, user)
                .stream()
                .map(Nomination::getId)
                .collect(Collectors.toSet());
    }
}