package ru.itis.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.model.MovieNight;
import ru.itis.model.Nomination;
import ru.itis.model.enums.MovieNightStatus;
import ru.itis.repository.MovieNightRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieNightScheduler {
    private static final int DEFAULT_DURATION_MINUTES = 120;
    private final MovieNightRepository movieNightRepository;
    private final CacheManager cacheManager;

    @Scheduled(fixedRate = 60_000) // спринг вызывает этот метод каждые 60 секунд
    @Transactional
    public void updateStatuses() {
        LocalDateTime now = LocalDateTime.now();
        updatePlanned(now);
        updateActive(now);
    }

    private void updatePlanned(LocalDateTime now) {
        List<MovieNight> planned = movieNightRepository.findByStatus(MovieNightStatus.PLANNED);
        for (MovieNight mn : planned) {
            if (mn.getEventDate().isAfter(now)) {
                continue;
            }
            Optional<Nomination> winner = mn.getNominations().stream()
                    .filter(Nomination::isWinner)
                    .findFirst();
            if (winner.isPresent()) {
                mn.setStatus(MovieNightStatus.ACTIVE);
                movieNightRepository.save(mn);
                evictCache(mn.getId());
                log.info("movieNight {} → ACTIVE (winner: {})", mn.getId(), winner.get().getMovie().getTitle());
                // winner.get() достает nomination из optional обертки
            } else if (now.isAfter(mn.getEventDate().plusMinutes(DEFAULT_DURATION_MINUTES))) {
                mn.setStatus(MovieNightStatus.FINISHED);
                movieNightRepository.save(mn);
                evictCache(mn.getId());
                log.info("movieNight {} → FINISHED (no winner, 2h passed)", mn.getId());
            }
        }
    }

    private void updateActive(LocalDateTime now) {
        List<MovieNight> active = movieNightRepository.findByStatus(MovieNightStatus.ACTIVE);
        for (MovieNight mn : active) {
            Optional<Nomination> winner = mn.getNominations().stream()
                    .filter(Nomination::isWinner)
                    .findFirst();
            if (winner.isPresent()) {
                int duration = winner.get().getMovie().getDurationMinutes() != null
                        ? winner.get().getMovie().getDurationMinutes()
                        : DEFAULT_DURATION_MINUTES;
                if (now.isAfter(mn.getEventDate().plusMinutes(duration))) {
                    mn.setStatus(MovieNightStatus.FINISHED);
                    movieNightRepository.save(mn);
                    evictCache(mn.getId());
                    log.info("movieNight {} → FINISHED (film ended)", mn.getId());
                }
            }
        }
    }

    private void evictCache(Long id) {
        Cache cache = cacheManager.getCache("movie-nights");
        // cacheManager - спринг-бин, который управляет всеми кэшами
        if (cache != null) {
            cache.evict(id);
        }
        // пишем удаление напрямую, тк планировщик пишет в бд минуя сервисы
        // где есть аннотация
    }
}