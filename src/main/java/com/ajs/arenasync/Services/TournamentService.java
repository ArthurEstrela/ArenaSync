package com.ajs.arenasync.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Optional<Tournament> findById(Long id) {
        return tournamentRepository.findById(id);
    }

    public void deleteById(Long id) {
        tournamentRepository.deleteById(id);
    }
}