package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.TournamentRequestDTO;
import com.ajs.arenasync.DTO.TournamentResponseDTO;
import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.ajs.arenasync.Entities.Organizer;
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

import org.springframework.data.domain.Page; // Importe Page
import org.springframework.data.domain.PageImpl; // Importe PageImpl para mockar
import org.springframework.data.domain.PageRequest; // Importe PageRequest
import org.springframework.data.domain.Pageable; // Importe Pageable

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
    private OrganizerRepository organizerRepository;

    @InjectMocks
    private TournamentService tournamentService;

    private Organizer organizer;
    private Tournament tournament;
    private TournamentRequestDTO tournamentRequestDTO;
    private Long organizerId = 1L;
    private Long tournamentId = 1L;

    @BeforeEach
    void setUp() {
        organizer = new Organizer();
        organizer.setId(organizerId);
        organizer.setName("Test Organizer");

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
            saved.setId(tournamentId);
            return saved;
        });

        TournamentResponseDTO responseDTO = tournamentService.saveTournament(organizerId, tournamentRequestDTO);

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
            tournamentService.saveTournament(organizerId, tournamentRequestDTO);
        });

        verify(organizerRepository, times(1)).findById(organizerId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testCreateTournament_EndDateBeforeStartDate() {
        tournamentRequestDTO.setEndDate(LocalDate.now().minusDays(1));
        tournamentRequestDTO.setStartDate(LocalDate.now());

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));


        assertThrows(BusinessException.class, () -> {
            tournamentService.saveTournament(organizerId, tournamentRequestDTO);
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
        tournament.setStatus(TournamentStatus.PENDING);
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
        tournament.setStatus(TournamentStatus.ONGOING);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(BusinessException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        tournament.setStatus(TournamentStatus.FINISHED);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        assertThrows(BusinessException.class, () -> {
            tournamentService.startTournament(tournamentId);
        });

        verify(tournamentRepository, times(2)).findById(tournamentId);
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
        tournament.setStatus(TournamentStatus.PENDING);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(BusinessException.class, () -> {
            tournamentService.finishTournament(tournamentId);
        });

        tournament.setStatus(TournamentStatus.FINISHED);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
         assertThrows(BusinessException.class, () -> {
            tournamentService.finishTournament(tournamentId);
        });

        verify(tournamentRepository, times(2)).findById(tournamentId);
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testGetAllTournaments_Paged() {
        // Crie uma lista de torneios para a página
        List<Tournament> tournaments = Collections.singletonList(tournament);
        // Crie um objeto Page (PageImpl é a implementação concreta)
        Pageable pageable = PageRequest.of(0, 10); // Página 0, tamanho 10
        Page<Tournament> tournamentPage = new PageImpl<>(tournaments, pageable, 1);

        when(tournamentRepository.findAll(pageable)).thenReturn(tournamentPage);

        Page<TournamentResponseDTO> responsePage = tournamentService.getAllTournaments(pageable);

        assertNotNull(responsePage);
        assertFalse(responsePage.isEmpty());
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(tournament.getName(), responsePage.getContent().get(0).getName());
        verify(tournamentRepository, times(1)).findAll(pageable);
    }

    @Test
    void testUpdateTournament_Success() {
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> {
            Tournament updated = invocation.getArgument(0);
            updated.setId(tournamentId);
            return updated;
        });


        TournamentResponseDTO responseDTO = tournamentService.updateTournament(tournamentId, tournamentRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(tournamentRequestDTO.getName(), responseDTO.getName());
        assertEquals(tournamentRequestDTO.getModality(), responseDTO.getModality());
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