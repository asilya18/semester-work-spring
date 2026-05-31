package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.model.Nomination;
import ru.itis.model.User;
import ru.itis.model.Vote;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByNominationAndUser(Nomination nomination, User user);

    boolean existsByNominationAndUser(Nomination nomination, User user);
    // голосовал ли юзер за эту номинацию

    long countByNomination(Nomination nomination);
}