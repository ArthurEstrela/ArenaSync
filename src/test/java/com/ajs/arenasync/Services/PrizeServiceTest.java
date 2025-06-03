package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.PrizeRequestDTO;
import com.ajs.arenasync.DTO.PrizeResponseDTO;
import com.ajs.arenasync.Entities.Prize;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PrizeRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;
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
public class PrizeServiceTest {

    @Mock
    private PrizeRepository prizeRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private PrizeService prizeService;

    private Tournament tournament;
    private Prize prize;
    private PrizeRequestDTO prizeRequestDTO;
    private Long tournamentId = 1L;
    private Long prizeId = 1L;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");

        prize = new Prize();
        prize.setId(prizeId);
        prize.setDescription("First Place Prize");
        prize.setValue(100.0);
        prize.setTournament(tournament);

        prizeRequestDTO = new PrizeRequestDTO();
        prizeRequestDTO.setTournamentId(tournamentId);
        prizeRequestDTO.setDescription("Grand Prize DTO");
        prizeRequestDTO.setValue(150.0);
    }

    @Test
    void testSave_Success() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(prizeRepository.save(any(Prize.class))).thenAnswer(invocation -> {
            Prize saved = invocation.getArgument(0);
            saved.setId(prizeId);
            return saved;
        });

        PrizeResponseDTO responseDTO = prizeService.save(prizeRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(prizeRequestDTO.getDescription(), responseDTO.getDescription());
        assertEquals(tournament.getName(), responseDTO.getTournamentName());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(prizeRepository, times(1)).save(any(Prize.class));
    }

    @Test
    void testSave_TournamentNotFound() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());
        // A validação do tournamentId no DTO é feita primeiro, mas a busca no repo também é validada

        assertThrows(BadRequestException.class, () -> { // Service lança BadRequest para torneio não encontrado
            prizeService.save(prizeRequestDTO);
        });

        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(prizeRepository, never()).save(any(Prize.class));
    }

    @Test
    void testSave_DescriptionNull() {
        prizeRequestDTO.setDescription(null);
        assertThrows(BadRequestException.class, () -> {
            prizeService.save(prizeRequestDTO);
        });
        verify(prizeRepository, never()).save(any(Prize.class));
    }

    @Test
    void testSave_DescriptionEmpty() {
        prizeRequestDTO.setDescription("   "); // Empty after trim
        assertThrows(BadRequestException.class, () -> {
            prizeService.save(prizeRequestDTO);
        });
        verify(prizeRepository, never()).save(any(Prize.class));
    }

    @Test
    void testSave_ValueNull() {
        prizeRequestDTO.setValue(null);
        assertThrows(BadRequestException.class, () -> {
            prizeService.save(prizeRequestDTO);
        });
        verify(prizeRepository, never()).save(any(Prize.class));
    }

    @Test
    void testSave_ValueZero() {
        prizeRequestDTO.setValue(0.0);
        assertThrows(BadRequestException.class, () -> {
            prizeService.save(prizeRequestDTO);
        });
        verify(prizeRepository, never()).save(any(Prize.class));
    }
    
    @Test
    void testSave_ValueNegative() {
        prizeRequestDTO.setValue(-10.0);
        assertThrows(BadRequestException.class, () -> {
            prizeService.save(prizeRequestDTO);
        });
        verify(prizeRepository, never()).save(any(Prize.class));
    }


    @Test
    void testSave_TournamentIdNullInDTO() {
        prizeRequestDTO.setTournamentId(null);
        assertThrows(BadRequestException.class, () -> {
            prizeService.save(prizeRequestDTO);
        });
        verify(tournamentRepository, never()).findById(anyLong());
        verify(prizeRepository, never()).save(any(Prize.class));
    }

    @Test
    void testFindById_Success() {
        when(prizeRepository.findById(prizeId)).thenReturn(Optional.of(prize));

        PrizeResponseDTO responseDTO = prizeService.findById(prizeId);

        assertNotNull(responseDTO);
        assertEquals(prize.getDescription(), responseDTO.getDescription());
        assertEquals(tournament.getName(), responseDTO.getTournamentName());
        verify(prizeRepository, times(1)).findById(prizeId);
    }

    @Test
    void testFindById_NotFound() {
        when(prizeRepository.findById(prizeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            prizeService.findById(prizeId);
        });
        verify(prizeRepository, times(1)).findById(prizeId);
    }

    @Test
    void testFindAll_Success() {
        when(prizeRepository.findAll()).thenReturn(Collections.singletonList(prize));

        List<PrizeResponseDTO> prizes = prizeService.findAll();

        assertNotNull(prizes);
        assertFalse(prizes.isEmpty());
        assertEquals(1, prizes.size());
        assertEquals(prize.getDescription(), prizes.get(0).getDescription());
        verify(prizeRepository, times(1)).findAll();
    }
    
    @Test
    void testFindAll_Empty() {
        when(prizeRepository.findAll()).thenReturn(Collections.emptyList());

        List<PrizeResponseDTO> prizes = prizeService.findAll();

        assertNotNull(prizes);
        assertTrue(prizes.isEmpty());
        verify(prizeRepository, times(1)).findAll();
    }

    @Test
    void testDeleteById_Success() {
        when(prizeRepository.existsById(prizeId)).thenReturn(true);
        doNothing().when(prizeRepository).deleteById(prizeId);

        assertDoesNotThrow(() -> prizeService.deleteById(prizeId));

        verify(prizeRepository, times(1)).existsById(prizeId);
        verify(prizeRepository, times(1)).deleteById(prizeId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(prizeRepository.existsById(prizeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            prizeService.deleteById(prizeId);
        });
        verify(prizeRepository, times(1)).existsById(prizeId);
        verify(prizeRepository, never()).deleteById(anyLong());
    }
}