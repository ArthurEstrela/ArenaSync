package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.EnrollmentRequestDTO;
import com.ajs.arenasync.DTO.EnrollmentResponseDTO;
import com.ajs.arenasync.Entities.Enrollment;
import com.ajs.arenasync.Entities.Enums.Status;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.EnrollmentRepository;
import com.ajs.arenasync.Repositories.TeamRepository;
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
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Enrollment enrollment;
    private Team team;
    private Tournament tournament;
    private EnrollmentRequestDTO enrollmentRequestDTO;
    private Long enrollmentId = 1L;
    private Long teamId = 1L;
    private Long tournamentId = 1L;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(teamId);
        team.setName("Test Team");

        tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");

        enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setTeam(team);
        enrollment.setTournament(tournament);
        enrollment.setStatus(Status.PENDING);

        enrollmentRequestDTO = new EnrollmentRequestDTO();
        enrollmentRequestDTO.setTeamId(teamId);
        enrollmentRequestDTO.setTournamentId(tournamentId);
        enrollmentRequestDTO.setStatus(Status.APPROVED);
    }

    @Test
    void testSaveFromDTO_Success() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(enrollmentRepository.existsByTeamAndTournament(team, tournament)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment saved = invocation.getArgument(0);
            saved.setId(enrollmentId); // Simula ID
            return saved;
        });

        EnrollmentResponseDTO responseDTO = enrollmentService.saveFromDTO(enrollmentRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(team.getName(), responseDTO.getTeamName());
        assertEquals(tournament.getName(), responseDTO.getTournamentName());
        assertEquals(enrollmentRequestDTO.getStatus().name(), responseDTO.getStatus());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void testSaveFromDTO_TeamNotFound() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());
        // Não precisa mockar tournamentRepository ou enrollmentRepository se o time não for encontrado

        assertThrows(BadRequestException.class, () -> {
            enrollmentService.saveFromDTO(enrollmentRequestDTO);
        });
        verify(teamRepository, times(1)).findById(teamId);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testSaveFromDTO_TournamentNotFound() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team)); // Time encontrado
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            enrollmentService.saveFromDTO(enrollmentRequestDTO);
        });
        verify(teamRepository, times(1)).findById(teamId);
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testSaveFromDTO_StatusNull() {
        enrollmentRequestDTO.setStatus(null);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        // A validação do status ocorre no método validateEnrollment.

        assertThrows(BadRequestException.class, () -> {
            enrollmentService.saveFromDTO(enrollmentRequestDTO);
        });
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }
    
    @Test
    void testSaveFromDTO_TeamIdNullInDTO() {
        enrollmentRequestDTO.setTeamId(null);
        // A validação ocorre em toEntity, que lança BadRequestException
        assertThrows(BadRequestException.class, () -> {
            enrollmentService.saveFromDTO(enrollmentRequestDTO);
        });
         verify(teamRepository, never()).findById(anyLong());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testSaveFromDTO_TournamentIdNullInDTO() {
        enrollmentRequestDTO.setTournamentId(null);
        // A validação ocorre em toEntity, que lança BadRequestException
        // Precisa mockar teamRepository para passar da primeira checagem em toEntity
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team)); 
        
        assertThrows(BadRequestException.class, () -> {
            enrollmentService.saveFromDTO(enrollmentRequestDTO);
        });
        verify(tournamentRepository, never()).findById(anyLong());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }


    @Test
    void testSaveFromDTO_EnrollmentAlreadyExists() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(enrollmentRepository.existsByTeamAndTournament(team, tournament)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            enrollmentService.saveFromDTO(enrollmentRequestDTO);
        });
        verify(enrollmentRepository, times(1)).existsByTeamAndTournament(team, tournament);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }


    @Test
    void testFindById_Success() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        EnrollmentResponseDTO responseDTO = enrollmentService.findById(enrollmentId);

        assertNotNull(responseDTO);
        assertEquals(team.getName(), responseDTO.getTeamName());
        assertEquals(tournament.getName(), responseDTO.getTournamentName());
        verify(enrollmentRepository, times(1)).findById(enrollmentId);
    }

    @Test
    void testFindById_NotFound() {
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            enrollmentService.findById(enrollmentId);
        });
        verify(enrollmentRepository, times(1)).findById(enrollmentId);
    }

    @Test
    void testFindAll() {
        when(enrollmentRepository.findAll()).thenReturn(Collections.singletonList(enrollment));

        List<EnrollmentResponseDTO> enrollments = enrollmentService.findAll();

        assertNotNull(enrollments);
        assertFalse(enrollments.isEmpty());
        assertEquals(1, enrollments.size());
        assertEquals(team.getName(), enrollments.get(0).getTeamName());
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void testDeleteById_Success() {
        when(enrollmentRepository.existsById(enrollmentId)).thenReturn(true);
        doNothing().when(enrollmentRepository).deleteById(enrollmentId);

        assertDoesNotThrow(() -> enrollmentService.deleteById(enrollmentId));

        verify(enrollmentRepository, times(1)).existsById(enrollmentId);
        verify(enrollmentRepository, times(1)).deleteById(enrollmentId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(enrollmentRepository.existsById(enrollmentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            enrollmentService.deleteById(enrollmentId);
        });
        verify(enrollmentRepository, times(1)).existsById(enrollmentId);
        verify(enrollmentRepository, never()).deleteById(anyLong());
    }
}