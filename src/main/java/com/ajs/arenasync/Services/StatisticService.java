package com.ajs.arenasync.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Entities.Player;
import com.ajs.arenasync.Entities.Statistic;
import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PlayerRepository;
import com.ajs.arenasync.Repositories.StatisticRepository;
import com.ajs.arenasync.Repositories.MatchRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "statistics")
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @CacheEvict(value = {"statistics"}, allEntries = true)
    public StatisticResponseDTO save(StatisticRequestDTO dto) {
        validateStatistic(dto);

        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new BadRequestException("Jogador não encontrado."));
        
        Match match = null;
        if (dto.getMatchId() != null) {
            match = matchRepository.findById(dto.getMatchId())
                    .orElseThrow(() -> new BadRequestException("Partida não encontrada para a estatística."));
        }

        Statistic statistic = new Statistic();
        statistic.setGamesPlayed(dto.getGamesPlayed());
        statistic.setWins(dto.getWins());
        statistic.setScore(dto.getScore());
        statistic.setAssists(dto.getAssists()); // Mapeia assists do DTO para a entidade
        statistic.setPlayer(player);
        statistic.setMatch(match);

        return toResponseDTO(statisticRepository.save(statistic));
    }

    @Cacheable(key = "#id")
    public StatisticResponseDTO findById(Long id) {
        Statistic statistic = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estatística", id));
        return toResponseDTO(statistic);
    }

    @Cacheable
    public List<StatisticResponseDTO> findAll() {
        return statisticRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"statistics"}, allEntries = true)
    public StatisticResponseDTO update(Long id, StatisticRequestDTO dto) {
        validateStatistic(dto);

        Statistic existing = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estatística", id));

        existing.setGamesPlayed(dto.getGamesPlayed());
        existing.setWins(dto.getWins());
        existing.setScore(dto.getScore());
        existing.setAssists(dto.getAssists()); // Mapeia assists do DTO para a entidade
        
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new BadRequestException("Jogador não encontrado."));
        existing.setPlayer(player);

        Match match = null;
        if (dto.getMatchId() != null) {
            match = matchRepository.findById(dto.getMatchId())
                    .orElseThrow(() -> new BadRequestException("Partida não encontrada para a estatística."));
        }
        existing.setMatch(match);

        return toResponseDTO(statisticRepository.save(existing));
    }

    @Caching(evict = {
        @CacheEvict(key = "#id"),
        @CacheEvict(allEntries = true)
    })
    public void deleteById(Long id) {
        Statistic existing = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estatística", id));
        statisticRepository.delete(existing);
    }

    private void validateStatistic(StatisticRequestDTO dto) {
        if (dto.getPlayerId() == null) {
            throw new BadRequestException("O ID do jogador é obrigatório.");
        }

        if (dto.getGamesPlayed() < 0 || dto.getWins() < 0 || dto.getScore() < 0 || dto.getAssists() < 0) { // Adicionado assists na validação de negativo
            throw new BadRequestException("Valores não podem ser negativos.");
        }

        if (dto.getWins() > dto.getGamesPlayed()) {
            throw new BadRequestException("Vitórias não podem exceder o número de partidas jogadas.");
        }
    }

    private StatisticResponseDTO toResponseDTO(Statistic statistic) {
        StatisticResponseDTO dto = new StatisticResponseDTO();
        dto.setId(statistic.getId());
        dto.setGamesPlayed(statistic.getGamesPlayed());
        dto.setWins(statistic.getWins());
        dto.setScore(statistic.getScore());
        dto.setAssists(statistic.getAssists()); // Mapeia assists da entidade para o DTO
        
        dto.setPlayerId(statistic.getPlayer() != null ? statistic.getPlayer().getId() : null);
        dto.setMatchId(statistic.getMatch() != null ? statistic.getMatch().getId() : null);

        dto.setPlayerName(statistic.getPlayer().getName());
        dto.setMatchInfo(statistic.getMatch() != null ? "Partida ID: " + statistic.getMatch().getId() : null);
        return dto;
    }
}
