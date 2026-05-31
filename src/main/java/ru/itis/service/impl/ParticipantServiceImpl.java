package ru.itis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.exception.AlreadyJoinedException;
import ru.itis.exception.ForbiddenException;
import ru.itis.exception.NotFoundException;
import ru.itis.model.MovieNight;
import ru.itis.model.Participant;
import ru.itis.model.User;
import ru.itis.model.enums.ParticipantRole;
import ru.itis.repository.MovieNightRepository;
import ru.itis.repository.ParticipantRepository;
import ru.itis.service.ParticipantService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;
    private final MovieNightRepository movieNightRepository;

    @Override
    @Transactional
    public void join(Long movieNightId, User user) {
        MovieNight movieNight = movieNightRepository.findById(movieNightId)
                .orElseThrow(() -> new NotFoundException("киновечер не найден"));
        if (participantRepository.existsByUserAndMovieNight(user, movieNight)) {
            log.error("user {} already joined movie night {}", user.getId(), movieNightId);
            throw new AlreadyJoinedException("вы уже участвуете в этом киновечере");
        }
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setMovieNight(movieNight);
        participant.setRole(ParticipantRole.PARTICIPANT);
        participantRepository.save(participant);
    }

    @Override
    @Transactional
    public void leave(Long movieNightId, User user) {
        MovieNight movieNight = movieNightRepository.findById(movieNightId)
                .orElseThrow(() -> new NotFoundException("киновечер не найден"));
        Participant participant = participantRepository.findByUserAndMovieNight(user, movieNight)
                .orElseThrow(() -> new NotFoundException("вы не являетесь участником этого киновечера"));
        if (participant.getRole() == ParticipantRole.HOST) {
            throw new ForbiddenException("создатель не может покинуть свой киновечер");
        }
        participantRepository.delete(participant);
    }

    @Override
    public boolean isParticipant(User user, MovieNight movieNight) {
        return participantRepository.existsByUserAndMovieNight(user, movieNight);
    }
}