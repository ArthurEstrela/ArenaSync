package com.ajs.arenasync.Services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.MatchRepository;
import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public Match save(Match match) {
        validateMatch(match);
        return matchRepository.save(match);
    }

    public Match findById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida", id));
    }

    public List<Match> findAll() {
    return matchRepository.findAll();
}


    public void deleteById(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Partida", id);
        }
        matchRepository.deleteById(id);
    }

    private void validateMatch(Match match) {
        if (match.getTeamA() == null || match.getTeamB() == null) {
            throw new BadRequestException("Ambas as equipes (A e B) devem ser informadas.");
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
