package ru.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.model.MovieNight;
import ru.itis.model.User;
import ru.itis.model.enums.MovieNightStatus;

import java.util.List;

public interface MovieNightRepository extends JpaRepository<MovieNight, Long> {
    List<MovieNight> findByCreator(User creator);

    List<MovieNight> findByStatus(MovieNightStatus status);

    // все киновечера где юзер является участником
    @Query("select mn from MovieNight mn join mn.participants p where p.user = :user")
    // participans берется из поля MN
    List<MovieNight> findAllByParticipant(@Param("user") User user);
}