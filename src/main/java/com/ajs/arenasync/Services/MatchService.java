package com.ajs.arenasync.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Repositories.MatchRepository;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public Match save(Match match) {
        return matchRepository.save(match);
    }

    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }

    public void deleteById(Long id) {
        matchRepository.deleteById(id);
    }
}
