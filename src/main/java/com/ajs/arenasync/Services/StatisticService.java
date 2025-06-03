package com.ajs.arenasync.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Entities.Player;
import com.ajs.arenasync.Entities.Statistic;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PlayerRepository;
import com.ajs.arenasync.Repositories.StatisticRepository;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public StatisticResponseDTO save(StatisticRequestDTO dto) {
        validateStatistic(dto);

        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new BadRequestException("Jogador não encontrado."));

        Statistic statistic = new Statistic();
        statistic.setGamesPlayed(dto.getGamesPlayed());
        statistic.setWins(dto.getWins());
        statistic.setScore(dto.getScore());
        statistic.setPlayer(player);

        return toResponseDTO(statisticRepository.save(statistic));
    }

    public StatisticResponseDTO findById(Long id) {
        Statistic statistic = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estatística", id));
        return toResponseDTO(statistic);
    }

    public StatisticResponseDTO update(Long id, StatisticRequestDTO dto) {
        Statistic existing = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estatística", id));

        existing.setGamesPlayed(dto.getGamesPlayed());
        existing.setWins(dto.getWins());
        existing.setScore(dto.getScore());

        // Regras: se não quiser permitir trocar o player, remova essa parte
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new BadRequestException("Jogador não encontrado."));
        existing.setPlayer(player);

        return toResponseDTO(statisticRepository.save(existing));
    }

    public void deleteById(Long id) {
        Statistic existing = statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estatística", id));
        statisticRepository.delete(existing);
    }

    private void validateStatistic(StatisticRequestDTO dto) {
        if (dto.getPlayerId() == null) {
            throw new BadRequestException("O ID do jogador é obrigatório.");
        }

        if (dto.getGamesPlayed() < 0 || dto.getWins() < 0 || dto.getScore() < 0) {
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
        dto.setPlayerName(statistic.getPlayer().getName());
        return dto;
    }
}