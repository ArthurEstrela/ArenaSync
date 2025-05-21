package com.ajs.arenasync.Services;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    public Tournament save(Tournament tournament) {
        // Define status inicial como PENDENTE
        tournament.setStatus(TournamentStatus.PENDING);

        return tournamentRepository.save(tournament);
    }

    public Tournament findById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
    }

    public void deleteById(Long id) {
        Tournament tournament = findById(id);
        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            throw new BusinessException("Não é possível excluir um torneio já finalizado.");
        }
        tournamentRepository.deleteById(id);
    }

    public Tournament startTournament(Long id) {
        Tournament tournament = findById(id);
        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new BusinessException("Apenas torneios pendentes podem ser iniciados.");
        }
        tournament.setStatus(TournamentStatus.ONGOING);
        return tournamentRepository.save(tournament);
    }

    public Tournament finishTournament(Long id) {
        Tournament tournament = findById(id);
        if (tournament.getStatus() != TournamentStatus.ONGOING) {
            throw new BusinessException("Apenas torneios em andamento podem ser finalizados.");
        }
        tournament.setStatus(TournamentStatus.FINISHED);
        return tournamentRepository.save(tournament);
    }
}