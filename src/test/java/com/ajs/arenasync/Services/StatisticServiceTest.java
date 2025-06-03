package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.StatisticRequestDTO;
import com.ajs.arenasync.DTO.StatisticResponseDTO;
import com.ajs.arenasync.Entities.Player;
import com.ajs.arenasync.Entities.Statistic;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PlayerRepository;
import com.ajs.arenasync.Repositories.StatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceTest {

    @Mock
    private StatisticRepository statisticRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private StatisticService statisticService;

    private Player player;
    private Statistic statistic;
    private StatisticRequestDTO statisticRequestDTO;
    private Long playerId = 1L;
    private Long statisticId = 1L;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(playerId);
        player.setName("Test Player");

        statistic = new Statistic();
        statistic.setId(statisticId);
        statistic.setGamesPlayed(10);
        statistic.setWins(5);
        statistic.setScore(100);
        statistic.setPlayer(player);

        statisticRequestDTO = new StatisticRequestDTO();
        statisticRequestDTO.setPlayerId(playerId);
        statisticRequestDTO.setGamesPlayed(10);
        statisticRequestDTO.setWins(5);
        statisticRequestDTO.setScore(100);
    }

    @Test
    void testSave_Success() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(statisticRepository.save(any(Statistic.class))).thenAnswer(invocation -> {
            Statistic saved = invocation.getArgument(0);
            saved.setId(statisticId);
            return saved;
        });

        StatisticResponseDTO responseDTO = statisticService.save(statisticRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(statisticRequestDTO.getGamesPlayed(), responseDTO.getGamesPlayed());
        assertEquals(player.getName(), responseDTO.getPlayerName());
        verify(playerRepository, times(1)).findById(playerId);
        verify(statisticRepository, times(1)).save(any(Statistic.class));
    }

    @Test
    void testSave_PlayerNotFound() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            statisticService.save(statisticRequestDTO);
        });

        verify(playerRepository, times(1)).findById(playerId);
        verify(statisticRepository, never()).save(any(Statistic.class));
    }

    @Test
    void testSave_PlayerIdNull() {
        statisticRequestDTO.setPlayerId(null);
        // A validação no service deve lançar BadRequestException
        assertThrows(BadRequestException.class, () -> {
            statisticService.save(statisticRequestDTO);
        });
        verify(playerRepository, never()).findById(anyLong());
        verify(statisticRepository, never()).save(any(Statistic.class));
    }

    @Test
    void testSave_NegativeValues() {
        statisticRequestDTO.setGamesPlayed(-1);
        // A validação no service deve lançar BadRequestException
        assertThrows(BadRequestException.class, () -> {
            statisticService.save(statisticRequestDTO);
        });

        statisticRequestDTO.setGamesPlayed(10); // reset
        statisticRequestDTO.setWins(-1);
        assertThrows(BadRequestException.class, () -> {
            statisticService.save(statisticRequestDTO);
        });
        verify(playerRepository, never()).findById(anyLong()); // Não deve chegar a buscar o player
        verify(statisticRepository, never()).save(any(Statistic.class));
    }

    @Test
    void testSave_WinsGreaterThanGamesPlayed() {
        statisticRequestDTO.setWins(11); // gamesPlayed é 10
        statisticRequestDTO.setGamesPlayed(10);

        // A validação no service deve lançar BadRequestException
        assertThrows(BadRequestException.class, () -> {
            statisticService.save(statisticRequestDTO);
        });
        verify(playerRepository, never()).findById(anyLong());
        verify(statisticRepository, never()).save(any(Statistic.class));
    }


    @Test
    void testFindById_Success() {
        when(statisticRepository.findById(statisticId)).thenReturn(Optional.of(statistic));

        StatisticResponseDTO responseDTO = statisticService.findById(statisticId);

        assertNotNull(responseDTO);
        assertEquals(statistic.getGamesPlayed(), responseDTO.getGamesPlayed());
        assertEquals(player.getName(), responseDTO.getPlayerName());
        verify(statisticRepository, times(1)).findById(statisticId);
    }

    @Test
    void testFindById_NotFound() {
        when(statisticRepository.findById(statisticId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            statisticService.findById(statisticId);
        });
        verify(statisticRepository, times(1)).findById(statisticId);
    }

    @Test
    void testUpdate_Success() {
        StatisticRequestDTO updateDto = new StatisticRequestDTO();
        updateDto.setPlayerId(playerId); // Pode ou não mudar o jogador, aqui mantemos
        updateDto.setGamesPlayed(12);
        updateDto.setWins(6);
        updateDto.setScore(120);

        Statistic updatedStatistic = new Statistic();
        updatedStatistic.setId(statisticId);
        updatedStatistic.setPlayer(player);
        updatedStatistic.setGamesPlayed(updateDto.getGamesPlayed());
        updatedStatistic.setWins(updateDto.getWins());
        updatedStatistic.setScore(updateDto.getScore());

        when(statisticRepository.findById(statisticId)).thenReturn(Optional.of(statistic));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player)); // Assumindo que o player existe
        when(statisticRepository.save(any(Statistic.class))).thenReturn(updatedStatistic);

        StatisticResponseDTO responseDTO = statisticService.update(statisticId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(updateDto.getGamesPlayed(), responseDTO.getGamesPlayed());
        assertEquals(updateDto.getWins(), responseDTO.getWins());
        verify(statisticRepository, times(1)).findById(statisticId);
        verify(playerRepository, times(1)).findById(playerId);
        verify(statisticRepository, times(1)).save(any(Statistic.class));
    }

    @Test
    void testUpdate_StatisticNotFound() {
        StatisticRequestDTO updateDto = new StatisticRequestDTO();
        updateDto.setPlayerId(playerId);
        updateDto.setGamesPlayed(12);

        when(statisticRepository.findById(statisticId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            statisticService.update(statisticId, updateDto);
        });
        verify(statisticRepository, times(1)).findById(statisticId);
        verify(playerRepository, never()).findById(anyLong());
        verify(statisticRepository, never()).save(any(Statistic.class));
    }

    @Test
    void testUpdate_PlayerNotFoundForUpdate() {
        StatisticRequestDTO updateDto = new StatisticRequestDTO();
        Long newPlayerId = 2L;
        updateDto.setPlayerId(newPlayerId);
        updateDto.setGamesPlayed(12);

        when(statisticRepository.findById(statisticId)).thenReturn(Optional.of(statistic));
        when(playerRepository.findById(newPlayerId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            statisticService.update(statisticId, updateDto);
        });
        verify(statisticRepository, times(1)).findById(statisticId);
        verify(playerRepository, times(1)).findById(newPlayerId);
        verify(statisticRepository, never()).save(any(Statistic.class));
    }

    @Test
    void testDeleteById_Success() {
        when(statisticRepository.findById(statisticId)).thenReturn(Optional.of(statistic));
        doNothing().when(statisticRepository).delete(statistic);

        assertDoesNotThrow(() -> statisticService.deleteById(statisticId));

        verify(statisticRepository, times(1)).findById(statisticId);
        verify(statisticRepository, times(1)).delete(statistic);
    }

    @Test
    void testDeleteById_NotFound() {
        when(statisticRepository.findById(statisticId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            statisticService.deleteById(statisticId);
        });
        verify(statisticRepository, times(1)).findById(statisticId);
        verify(statisticRepository, never()).delete(any(Statistic.class));
    }
}