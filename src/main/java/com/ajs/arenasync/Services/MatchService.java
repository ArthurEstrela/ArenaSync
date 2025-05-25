package com.ajs.arenasync.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.MatchRequestDTO;
import com.ajs.arenasync.DTO.MatchResponseDTO;
import com.ajs.arenasync.Entities.LocationPlatform;
import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.LocationPlatformRepository;
import com.ajs.arenasync.Repositories.MatchRepository;
import com.ajs.arenasync.Repositories.TeamRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private LocationPlatformRepository locationPlatformRepository;

    public MatchResponseDTO saveFromDTO(MatchRequestDTO dto) {
        Match match = toEntity(dto);
        validateMatch(match);
        return toResponseDTO(matchRepository.save(match));
    }

    public MatchResponseDTO findById(Long id) {
        return toResponseDTO(matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida", id)));
    }

    public List<MatchResponseDTO> findAll() {
        return matchRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Partida", id);
        }
        matchRepository.deleteById(id);
    }

    private Match toEntity(MatchRequestDTO dto) {
        Team teamA = teamRepository.findById(dto.getTeamAId())
                .orElseThrow(() -> new BadRequestException("Equipe A não encontrada."));
        Team teamB = teamRepository.findById(dto.getTeamBId())
                .orElseThrow(() -> new BadRequestException("Equipe B não encontrada."));
        Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                .orElseThrow(() -> new BadRequestException("Torneio não encontrado."));
        LocationPlatform location = locationPlatformRepository.findById(dto.getLocationPlatformId())
                .orElseThrow(() -> new BadRequestException("Local/Plataforma não encontrado."));

        Match match = new Match();
        match.setTeamA(teamA);
        match.setTeamB(teamB);
        match.setTournament(tournament);
        match.setLocationPlatform(location);
        match.setScheduledDateTime(dto.getScheduledDateTime());
        match.setScoreTeamA(dto.getScoreTeamA());
        match.setScoreTeamB(dto.getScoreTeamB());
        return match;
    }

    private MatchResponseDTO toResponseDTO(Match match) {
        MatchResponseDTO dto = new MatchResponseDTO();
        dto.setId(match.getId());
        dto.setTeamAName(match.getTeamA().getName());
        dto.setTeamBName(match.getTeamB().getName());
        dto.setTournamentName(match.getTournament().getName());
        dto.setLocationPlatformName(match.getLocationPlatform().getName());
        dto.setScheduledDateTime(match.getScheduledDateTime());
        dto.setScoreTeamA(match.getScoreTeamA());
        dto.setScoreTeamB(match.getScoreTeamB());
        return dto;
    }

    private void validateMatch(Match match) {
        if (match.getTeamA() == null || match.getTeamB() == null) {
            throw new BadRequestException("Ambas as equipes devem ser informadas.");
        }

        if (match.getTeamA().equals(match.getTeamB())) {
            throw new BadRequestException("As equipes A e B devem ser diferentes.");
        }

        if (match.getScheduledDateTime() == null) {
            throw new BadRequestException("A data e hora da partida devem ser informadas.");
        }

        if (match.getScheduledDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("A data e hora da partida devem estar no futuro.");
        }

        if (match.getTournament() == null) {
            throw new BadRequestException("O torneio da partida deve ser informado.");
        }

        if (match.getLocationPlatform() == null) {
            throw new BadRequestException("A plataforma/local da partida deve ser informada.");
        }

        if (match.getScoreTeamA() != null && match.getScoreTeamA() < 0) {
            throw new BadRequestException("A pontuação da equipe A não pode ser negativa.");
        }

        if (match.getScoreTeamB() != null && match.getScoreTeamB() < 0) {
            throw new BadRequestException("A pontuação da equipe B não pode ser negativa.");
        }
    }
}