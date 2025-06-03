package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.ajs.arenasync.Entities.Organizer; // Alterado de User para Organizer
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.OrganizerRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private OrganizerRepository organizerRepository; // Mock para OrganizerRepository

    @InjectMocks
    private TournamentService tournamentService;

    private Organizer organizer; // Usar a entidade Organizer
    private Tournament tournament;
    private TournamentRequestDTO tournamentRequestDTO;
    private Long organizerId = 1L;
    private Long tournamentId = 1L;

    @BeforeEach
    void setUp() {
        organizer = new Organizer(); // Instanciar Organizer
        organizer.setId(organizerId);
        organizer.setName("Test Organizer");
        // Defina outros campos do Organizer se necessário para os testes

        tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");
        tournament.setModality("Chess");
        tournament.setRules("Standard rules");
        tournament.setStartDate(LocalDate.now().plusDays(1));
        tournament.setEndDate(LocalDate.now().plusDays(10));
        tournament.setType(TournamentType.ESPORT);
        tournament.setStatus(TournamentStatus.PENDING);
        tournament.setOrganizer(organizer);

        tournamentRequestDTO = new TournamentRequestDTO();
        tournamentRequestDTO.setName("Test Tournament DTO");
        tournamentRequestDTO.setModality("Chess DTO");
        tournamentRequestDTO.setRules("Standard rules DTO");
        tournamentRequestDTO.setStartDate(LocalDate.now().plusDays(2));
        tournamentRequestDTO.setEndDate(LocalDate.now().plusDays(12));
        tournamentRequestDTO.setType(TournamentType.SPORT);
    }

    @Test
    void testCreateTournament_Success() {
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> {
            Tournament saved = invocation.getArgument(0);
            saved.setId(tournamentId); // Simula a atribuição de ID ao salvar
            return saved;
        });

        TournamentResponseDTO responseDTO = tournamentService.createTournament(organizerId, tournamentRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(tournamentRequestDTO.getName(), responseDTO.getName());
        assertEquals(TournamentStatus.PENDING, responseDTO.getStatus());
        assertEquals(organizer.getName(), responseDTO.getOrganizerName());
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testCreateTournament_OrganizerNotFound() {
        when(organizerRepository.findById(organizerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.createTournament(organizerId, tournamentRequestDTO);
        });

        verify(organizerRepository, times(1)).findById(organizerId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testCreateTournament_EndDateBeforeStartDate() {
        tournamentRequestDTO.setEndDate(LocalDate.now().minusDays(1)); // Data de término inválida
        tournamentRequestDTO.setStartDate(LocalDate.now());

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));


        assertThrows(BusinessException.class, () -> {
            tournamentService.createTournament(organizerId, tournamentRequestDTO);
        });
        verify(organizerRepository, times(1)).findById(organizerId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }


    @Test
    void testFindById_Success() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        TournamentResponseDTO responseDTO = tournamentService.findById(tournamentId);

        assertNotNull(responseDTO);
        assertEquals(tournament.getName(), responseDTO.getName());
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void testFindById_NotFound() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.findById(tournamentId);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void testDeleteById_Success() {
        tournament.setStatus(TournamentStatus.PENDING); // Pode ser PENDING ou ONGOING
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        doNothing().when(tournamentRepository).deleteById(tournamentId);

        assertDoesNotThrow(() -> tournamentService.deleteById(tournamentId));

        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).deleteById(tournamentId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.deleteById(tournamentId);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteById_TournamentFinished() {
        tournament.setStatus(TournamentStatus.FINISHED);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(BusinessException.class, () -> {
            tournamentService.deleteById(tournamentId);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, never()).deleteById(anyLong());
    }

    @Test
    void testStartTournament_Success() {
        tournament.setStatus(TournamentStatus.PENDING);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TournamentResponseDTO responseDTO = tournamentService.startTournament(tournamentId);

        assertNotNull(responseDTO);
        assertEquals(TournamentStatus.ONGOING, responseDTO.getStatus());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void testStartTournament_NotFound() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testStartTournament_NotPending() {
        tournament.setStatus(TournamentStatus.ONGOING); // Já iniciado
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(BusinessException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        tournament.setStatus(TournamentStatus.FINISHED); // Finalizado
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        assertThrows(BusinessException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        verify(tournamentRepository, times(2)).findById(tournamentId); // Chamado duas vezes nos asserts
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testFinishTournament_Success() {
        tournament.setStatus(TournamentStatus.ONGOING);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TournamentResponseDTO responseDTO = tournamentService.finishTournament(tournamentId);

        assertNotNull(responseDTO);
        assertEquals(TournamentStatus.FINISHED, responseDTO.getStatus());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void testFinishTournament_NotFound() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.finishTournament(tournamentId);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testFinishTournament_NotOngoing() {
        tournament.setStatus(TournamentStatus.PENDING); // Pendente
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(BusinessException.class, () -> {
            tournamentService.finishTournament(tournamentId);
        });

        tournament.setStatus(TournamentStatus.FINISHED); // Já finalizado
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
         assertThrows(BusinessException.class, () -> {
            tournamentService.finishTournament(tournamentId);
        });

        verify(tournamentRepository, times(2)).findById(tournamentId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testGetAllTournaments() {
        when(tournamentRepository.findAll()).thenReturn(Collections.singletonList(tournament));

        List<TournamentResponseDTO> tournaments = tournamentService.getAllTournaments();

        assertNotNull(tournaments);
        assertFalse(tournaments.isEmpty());
        assertEquals(1, tournaments.size());
        assertEquals(tournament.getName(), tournaments.get(0).getName());
        verify(tournamentRepository, times(1)).findAll();
    }

    @Test
    void testUpdateTournament_Success() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> {
            // Simula a atualização e retorna o objeto atualizado
            Tournament updated = invocation.getArgument(0);
            updated.setId(tournamentId); // Garante que o ID seja mantido
            return updated;
        });


        TournamentResponseDTO responseDTO = tournamentService.updateTournament(tournamentId, tournamentRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(tournamentRequestDTO.getName(), responseDTO.getName());
        assertEquals(tournamentRequestDTO.getModality(), responseDTO.getModality());
        // O status não deve ser alterado pelo update, mantém o original
        assertEquals(tournament.getStatus(), responseDTO.getStatus());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testUpdateTournament_NotFound() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tournamentService.updateTournament(tournamentId, tournamentRequestDTO);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }
}