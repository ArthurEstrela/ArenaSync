package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Entities.Organizer;
import com.ajs.arenasync.Entities.Tournament;
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

    public TournamentResponseDTO createTournament(Long organizerId, TournamentRequestDTO dto) {
    Organizer organizer = organizerRepository.findById(organizerId)
            .orElseThrow(() -> new ResourceNotFoundException("Organizer", organizerId));

  
    if (dto.getEndDate().isBefore(dto.getStartDate())) {
        throw new BusinessException("A data de término deve ser posterior ou igual à data de início.");
    }

    Tournament tournament = toEntity(dto);
    tournament.setOrganizer(organizer);
    tournament.setStatus(TournamentStatus.PENDING); // status inicial

    return toResponseDTO(tournamentRepository.save(tournament));
}

    // Buscar por ID
    public TournamentResponseDTO findById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
        return toResponseDTO(tournament);
    }

    // Deletar
    public void deleteById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));

        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            throw new BusinessException("Não é possível excluir um torneio já finalizado.");
        }

        tournamentRepository.deleteById(id);
    }

    // Iniciar
    public TournamentResponseDTO startTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));

        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new BusinessException("Apenas torneios pendentes podem ser iniciados.");
        }

        tournament.setStatus(TournamentStatus.ONGOING);
        return toResponseDTO(tournamentRepository.save(tournament));
    }

    // Finalizar
    public TournamentResponseDTO finishTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));

        if (tournament.getStatus() != TournamentStatus.ONGOING) {
            throw new BusinessException("Apenas torneios em andamento podem ser finalizados.");
        }

        tournament.setStatus(TournamentStatus.FINISHED);
        return toResponseDTO(tournamentRepository.save(tournament));
    }

    // Listar todos
    public List<TournamentResponseDTO> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Atualizar
    public TournamentResponseDTO updateTournament(Long id, TournamentRequestDTO dto) {
        Tournament existing = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));

        existing.setName(dto.getName());
        existing.setModality(dto.getModality());
        existing.setRules(dto.getRules());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setType(dto.getType());

        return toResponseDTO(tournamentRepository.save(existing));
    }

    // Conversão DTO -> Entidade
    private Tournament toEntity(TournamentRequestDTO dto) {
        Tournament t = new Tournament();
        t.setName(dto.getName());
        t.setModality(dto.getModality());
        t.setRules(dto.getRules());
        t.setStartDate(dto.getStartDate());
        t.setEndDate(dto.getEndDate());
        t.setType(dto.getType());
        return t;
    }

    // Conversão Entidade -> DTO
    private TournamentResponseDTO toResponseDTO(Tournament t) {
        TournamentResponseDTO dto = new TournamentResponseDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setModality(t.getModality());
        dto.setRules(t.getRules());
        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());
        dto.setType(t.getType());
        dto.setStatus(t.getStatus());
        dto.setOrganizerName(t.getOrganizer() != null ? t.getOrganizer().getName() : null);
        return dto;
    }
}