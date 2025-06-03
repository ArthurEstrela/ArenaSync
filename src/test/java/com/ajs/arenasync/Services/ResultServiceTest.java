package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.ResultRequestDTO;
import com.ajs.arenasync.DTO.ResultResponseDTO;
import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Entities.Result;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.MatchRepository;
import com.ajs.arenasync.Repositories.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private ResultService resultService;

    private Match match;
    private Result result;
    private ResultRequestDTO resultRequestDTO;
    private Long matchId = 1L;
    private Long resultId = 1L;

    @BeforeEach
    void setUp() {
        match = new Match();
        match.setId(matchId);
        // Configure outros campos do Match se necessário para o DTO de resposta

        result = new Result();
        result.setId(resultId);
        result.setMatch(match);
        result.setScoreTeamA(2);
        result.setScoreTeamB(1);

        resultRequestDTO = new ResultRequestDTO();
        resultRequestDTO.setMatchId(matchId);
        resultRequestDTO.setScoreTeamA(3);
        resultRequestDTO.setScoreTeamB(0);
    }

    @Test
    void testSave_Success() {
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(resultRepository.existsByMatchId(matchId)).thenReturn(false);
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> {
            Result saved = invocation.getArgument(0);
            saved.setId(resultId); // Simula atribuição de ID
            return saved;
        });

        ResultResponseDTO responseDTO = resultService.save(resultRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(resultRequestDTO.getMatchId(), responseDTO.getMatchId());
        assertEquals(resultRequestDTO.getScoreTeamA(), responseDTO.getScoreTeamA());
        verify(matchRepository, times(1)).findById(matchId);
        verify(resultRepository, times(1)).existsByMatchId(matchId);
        verify(resultRepository, times(1)).save(any(Result.class));
    }

    @Test
    void testSave_MatchNotFound() {
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        // A validação de matchId no DTO é feita antes, mas a busca no repo também é validada
        // Se o DTO estiver ok, mas o match não existir no banco:

        assertThrows(ResourceNotFoundException.class, () -> {
            resultService.save(resultRequestDTO);
        });

        verify(matchRepository, times(1)).findById(matchId);
        verify(resultRepository, never()).existsByMatchId(anyLong());
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    void testSave_MatchIdNullInDTO() {
        resultRequestDTO.setMatchId(null);
        // A validação no service deve lançar BadRequestException
        assertThrows(BadRequestException.class, () -> {
            resultService.save(resultRequestDTO);
        });
        verify(matchRepository, never()).findById(anyLong());
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    void testSave_ScoreTeamANull() {
        resultRequestDTO.setScoreTeamA(null);
         assertThrows(BadRequestException.class, () -> {
            resultService.save(resultRequestDTO);
        });
        verify(resultRepository, never()).save(any(Result.class));
    }
    
    @Test
    void testSave_ScoreTeamBNull() {
        resultRequestDTO.setScoreTeamB(null);
         assertThrows(BadRequestException.class, () -> {
            resultService.save(resultRequestDTO);
        });
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    void testSave_ScoreTeamANegative() {
        resultRequestDTO.setScoreTeamA(-1);
         assertThrows(BadRequestException.class, () -> {
            resultService.save(resultRequestDTO);
        });
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    void testSave_ScoreTeamBNegative() {
        resultRequestDTO.setScoreTeamB(-1);
         assertThrows(BadRequestException.class, () -> {
            resultService.save(resultRequestDTO);
        });
        verify(resultRepository, never()).save(any(Result.class));
    }

    @Test
    void testSave_ResultAlreadyExistsForMatch() {
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match)); // Match precisa ser encontrado primeiro
        when(resultRepository.existsByMatchId(matchId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            resultService.save(resultRequestDTO);
        });

        verify(matchRepository, times(1)).findById(matchId);
        verify(resultRepository, times(1)).existsByMatchId(matchId);
        verify(resultRepository, never()).save(any(Result.class));
    }


    @Test
    void testFindById_Success() {
        when(resultRepository.findById(resultId)).thenReturn(Optional.of(result));

        ResultResponseDTO responseDTO = resultService.findById(resultId);

        assertNotNull(responseDTO);
        assertEquals(result.getMatch().getId(), responseDTO.getMatchId());
        assertEquals(result.getScoreTeamA(), responseDTO.getScoreTeamA());
        verify(resultRepository, times(1)).findById(resultId);
    }

    @Test
    void testFindById_NotFound() {
        when(resultRepository.findById(resultId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            resultService.findById(resultId);
        });
        verify(resultRepository, times(1)).findById(resultId);
    }

    @Test
    void testFindAll_Success() {
        when(resultRepository.findAll()).thenReturn(Collections.singletonList(result));

        List<ResultResponseDTO> results = resultService.findAll();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(result.getScoreTeamA(), results.get(0).getScoreTeamA());
        verify(resultRepository, times(1)).findAll();
    }
    
    @Test
    void testFindAll_Empty() {
        when(resultRepository.findAll()).thenReturn(Collections.emptyList());

        List<ResultResponseDTO> results = resultService.findAll();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(resultRepository, times(1)).findAll();
    }


    @Test
    void testDeleteById_Success() {
        when(resultRepository.existsById(resultId)).thenReturn(true);
        doNothing().when(resultRepository).deleteById(resultId);

        assertDoesNotThrow(() -> resultService.deleteById(resultId));

        verify(resultRepository, times(1)).existsById(resultId);
        verify(resultRepository, times(1)).deleteById(resultId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(resultRepository.existsById(resultId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            resultService.deleteById(resultId);
        });
        verify(resultRepository, times(1)).existsById(resultId);
        verify(resultRepository, never()).deleteById(anyLong());
    }
}