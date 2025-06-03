package com.ajs.arenasync.Services;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private LocationPlatformRepository locationPlatformRepository;

    @InjectMocks
    private MatchService matchService;

    private Match match;
    private MatchRequestDTO matchRequestDTO;
    private Team teamA, teamB;
    private Tournament tournament;
    private LocationPlatform locationPlatform;
    private Long matchId = 1L;
    private Long teamAId = 1L;
    private Long teamBId = 2L;
    private Long tournamentId = 1L;
    private Long locationId = 1L;

    @BeforeEach
    void setUp() {
        teamA = new Team();
        teamA.setId(teamAId);
        teamA.setName("Team A");

        teamB = new Team();
        teamB.setId(teamBId);
        teamB.setName("Team B");

        tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");

        locationPlatform = new LocationPlatform();
        locationPlatform.setId(locationId);
        locationPlatform.setName("Online Platform");

        match = new Match();
        match.setId(matchId);
        match.setTeamA(teamA);
        match.setTeamB(teamB);
        match.setTournament(tournament);
        match.setLocationPlatform(locationPlatform);
        match.setScheduledDateTime(LocalDateTime.now().plusDays(1));
        match.setScoreTeamA(0); // Scores iniciais podem ser nulos ou 0
        match.setScoreTeamB(0);


        matchRequestDTO = new MatchRequestDTO();
        matchRequestDTO.setTeamAId(teamAId);
        matchRequestDTO.setTeamBId(teamBId);
        matchRequestDTO.setTournamentId(tournamentId);
        matchRequestDTO.setLocationPlatformId(locationId);
        matchRequestDTO.setScheduledDateTime(LocalDateTime.now().plusDays(2));
        matchRequestDTO.setScoreTeamA(null); // Scores podem ser nulos no DTO
        matchRequestDTO.setScoreTeamB(null);
    }

    @Test
    void testSaveFromDTO_Success() {
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(teamBId)).thenReturn(Optional.of(teamB));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match saved = invocation.getArgument(0);
            saved.setId(matchId);
            return saved;
        });

        MatchResponseDTO responseDTO = matchService.saveFromDTO(matchRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(teamA.getName(), responseDTO.getTeamAName());
        assertEquals(tournament.getName(), responseDTO.getTournamentName());
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void testSaveFromDTO_TeamANotFound() {
        when(teamRepository.findById(teamAId)).thenReturn(Optional.empty());
        // Não precisa mockar os outros, pois deve falhar antes

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        verify(teamRepository, times(1)).findById(teamAId);
        verify(matchRepository, never()).save(any(Match.class));
    }
    
    @Test
    void testSaveFromDTO_TeamBNotFound() {
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(teamBId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        verify(teamRepository, times(1)).findById(teamBId);
        verify(matchRepository, never()).save(any(Match.class));
    }
    
    @Test
    void testSaveFromDTO_TournamentNotFound() {
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(teamBId)).thenReturn(Optional.of(teamB));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(matchRepository, never()).save(any(Match.class));
    }
    
    @Test
    void testSaveFromDTO_LocationPlatformNotFound() {
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(teamBId)).thenReturn(Optional.of(teamB));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        verify(locationPlatformRepository, times(1)).findById(locationId);
        verify(matchRepository, never()).save(any(Match.class));
    }


    @Test
    void testSaveFromDTO_TeamAEqualsTeamB() {
        matchRequestDTO.setTeamBId(teamAId); // Mesmo ID para TeamA e TeamB
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        // Para o segundo findById (que seria para teamBId mas agora é teamAId)
        // o mock já cobre.
        // A validação ocorre após buscar as entidades Team.

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        // Verifica que ambos foram buscados (mesmo que seja o mesmo ID)
        verify(teamRepository, times(2)).findById(teamAId); 
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void testSaveFromDTO_ScheduledDateTimeNull() {
        matchRequestDTO.setScheduledDateTime(null);
        // A validação do `validateMatch` é chamada após `toEntity`.
        // Portanto, os repositórios para `toEntity` precisam ser mockados.
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(teamBId)).thenReturn(Optional.of(teamB));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void testSaveFromDTO_ScheduledDateTimeInPast() {
        matchRequestDTO.setScheduledDateTime(LocalDateTime.now().minusDays(1));
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(teamBId)).thenReturn(Optional.of(teamB));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        verify(matchRepository, never()).save(any(Match.class));
    }
    
    @Test
    void testSaveFromDTO_ScoreTeamANegative() {
        matchRequestDTO.setScoreTeamA(-1);
        when(teamRepository.findById(teamAId)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(teamBId)).thenReturn(Optional.of(teamB));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(locationPlatformRepository.findById(locationId)).thenReturn(Optional.of(locationPlatform));

        assertThrows(BadRequestException.class, () -> {
            matchService.saveFromDTO(matchRequestDTO);
        });
        verify(matchRepository, never()).save(any(Match.class));
    }


    @Test
    void testFindById_Success() {
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        MatchResponseDTO responseDTO = matchService.findById(matchId);

        assertNotNull(responseDTO);
        assertEquals(teamA.getName(), responseDTO.getTeamAName());
        assertEquals(locationPlatform.getName(), responseDTO.getLocationPlatformName());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    void testFindById_NotFound() {
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            matchService.findById(matchId);
        });
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    void testFindAll() {
        when(matchRepository.findAll()).thenReturn(Collections.singletonList(match));

        List<MatchResponseDTO> matches = matchService.findAll();

        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        assertEquals(1, matches.size());
        assertEquals(match.getTeamA().getName(), matches.get(0).getTeamAName());
        verify(matchRepository, times(1)).findAll();
    }

    @Test
    void testDeleteById_Success() {
        when(matchRepository.existsById(matchId)).thenReturn(true);
        doNothing().when(matchRepository).deleteById(matchId);

        assertDoesNotThrow(() -> matchService.deleteById(matchId));

        verify(matchRepository, times(1)).existsById(matchId);
        verify(matchRepository, times(1)).deleteById(matchId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(matchRepository.existsById(matchId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            matchService.deleteById(matchId);
        });
        verify(matchRepository, times(1)).existsById(matchId);
        verify(matchRepository, never()).deleteById(anyLong());
    }
}