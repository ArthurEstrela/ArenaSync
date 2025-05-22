package com.ajs.arenasync.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.OrganizerRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    // Criar torneio — somente se organizer existir
    public Tournament createTournament(Long organizerId, Tournament tournamentData) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer", organizerId));

        tournamentData.setOrganizer(organizer); // seta o organizador no torneio
        return tournamentRepository.save(tournamentData);
    }

    // Procurar Torneio pelo ID
    public Tournament findById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
    }

    // Deletar torneio pelo Id, somente se existir
    public void deleteById(Long id) {
        Tournament tournament = findById(id);
        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            throw new BusinessException("Não é possível excluir um torneio já finalizado.");
        }
        tournamentRepository.deleteById(id);
    }

    // Iniciar o Torneio
    public Tournament startTournament(Long id) {
        Tournament tournament = findById(id);
        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new BusinessException("Apenas torneios pendentes podem ser iniciados.");
        }
        tournament.setStatus(TournamentStatus.ONGOING);
        return tournamentRepository.save(tournament);
    }

    // Finalizar o torneio
    public Tournament finishTournament(Long id) {
        Tournament tournament = findById(id);
        if (tournament.getStatus() != TournamentStatus.ONGOING) {
            throw new BusinessException("Apenas torneios em andamento podem ser finalizados.");
        }
        tournament.setStatus(TournamentStatus.FINISHED);
        return tournamentRepository.save(tournament);
    }

    // Listar todos os torneios
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    // Atualizar torneio
    public Tournament updateTournament(Long id, Tournament updatedData) {
        Tournament existing = findById(id);
        existing.setName(updatedData.getName());
        existing.setModality(updatedData.getModality());
        existing.setRules(updatedData.getRules());
        existing.setStartDate(updatedData.getStartDate());
        existing.setEndDate(updatedData.getEndDate());
        existing.setType(updatedData.getType());

        return tournamentRepository.save(existing);
    }

}
