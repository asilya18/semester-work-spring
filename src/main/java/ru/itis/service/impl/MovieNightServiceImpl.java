package ru.itis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.MovieNightForm;
import ru.itis.exception.ForbiddenException;
import ru.itis.exception.NotFoundException;
import ru.itis.model.MovieNight;
import ru.itis.model.Participant;
import ru.itis.model.User;
import ru.itis.model.enums.MovieNightStatus;
import ru.itis.model.enums.ParticipantRole;
import ru.itis.repository.MovieNightRepository;
import ru.itis.repository.ParticipantRepository;
import ru.itis.service.MovieNightService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieNightServiceImpl implements MovieNightService {
    private final MovieNightRepository movieNightRepository;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional // тк сохраняем MovieNight + Participant
    public MovieNight create(MovieNightForm form, User creator) {
        MovieNight movieNight = new MovieNight();
        movieNight.setTitle(form.getTitle());
        movieNight.setDescription(form.getDescription());
        movieNight.setEventDate(form.getEventDate());
        movieNight.setPlatform(form.getPlatform());
        movieNight.setStatus(MovieNightStatus.PLANNED);
        movieNight.setCreator(creator);
        movieNight = movieNightRepository.save(movieNight);

        Participant host = new Participant();
        host.setUser(creator);
        host.setMovieNight(movieNight);
        host.setRole(ParticipantRole.HOST);
        participantRepository.save(host);

        return movieNight;
    }

    @Override
    @Cacheable(value = "movie-nights", key = "#id")
    public MovieNight findById(Long id) {
        return movieNightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("киновечер не найден"));
    }

    @Override
    public List<MovieNight> findAll() {
        return movieNightRepository.findAll();
    }

    @Override
    public List<MovieNight> findByCreator(User creator) {
        return movieNightRepository.findByCreator(creator);
    }

    @Override
    public List<MovieNight> findByParticipant(User user) {
        return movieNightRepository.findAllByParticipant(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "movie-nights", key = "#id")
    // @CacheEvict удаляет данные из редис при изменении
    public MovieNight update(Long id, MovieNightForm form, User currentUser) {
        MovieNight movieNight = movieNightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("киновечер не найден"));
        if (!movieNight.getCreator().equals(currentUser)) {
            log.error("user {} tried to edit movie night {} without permission", currentUser.getId(), id);
            throw new ForbiddenException("только создатель может редактировать киновечер");
        }
        movieNight.setTitle(form.getTitle());
        movieNight.setDescription(form.getDescription());
        movieNight.setEventDate(form.getEventDate());
        movieNight.setPlatform(form.getPlatform());
        return movieNightRepository.save(movieNight);
    }

    @Override
    @Transactional
    @CacheEvict(value = "movie-nights", key = "#id")
    public void delete(Long id, User currentUser) {
        MovieNight movieNight = movieNightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("киновечер не найден"));
        if (!movieNight.getCreator().equals(currentUser)) {
            log.error("user {} tried to delete movie night {} without permission", currentUser.getId(), id);
            throw new ForbiddenException("только создатель может удалить киновечер");
        }
        movieNightRepository.delete(movieNight);
    }

}