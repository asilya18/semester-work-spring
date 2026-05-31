package ru.itis.service;

import ru.itis.model.Nomination;
import ru.itis.model.User;
import ru.itis.model.Vote;

public interface VoteService {
    Vote vote(Long nominationId, User user);
    void unvote(Long nominationId, User user);
    boolean hasVoted(Nomination nomination, User user);
    long countVotes(Nomination nomination);
}