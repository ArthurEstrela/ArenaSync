package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.PlayerRequestDTO;
import com.ajs.arenasync.DTO.PlayerResponseDTO;
import com.ajs.arenasync.Entities.Player;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PlayerRepository;
import com.ajs.arenasync.Repositories.TeamRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player player;
    private Team team;
    private PlayerRequestDTO playerRequestDTO;
    private Long playerId = 1L;
    private Long teamId = 1L;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(teamId);
        team.setName("Test Team");

        player = new Player();
        player.setId(playerId);
        player.setName("Test Player");
        player.setEmail("test@example.com");
        // player.setTeam(team); // Associar nos testes específicos se necessário

        playerRequestDTO = new PlayerRequestDTO();
        playerRequestDTO.setName("Test Player DTO");
        playerRequestDTO.setEmail("testdto@example.com");
        // playerRequestDTO.setTeamId(teamId); // Associar nos testes específicos
    }

    @Test
    void testSaveFromDTO_Success_NoTeam() {
        playerRequestDTO.setTeamId(null); // Free agent
        when(playerRepository.existsByEmail(anyString())).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player saved = invocation.getArgument(0);
            saved.setId(playerId); // Simula ID
            return saved;
        });

        PlayerResponseDTO responseDTO = playerService.saveFromDTO(playerRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(playerRequestDTO.getName(), responseDTO.getName());
        assertNull(responseDTO.getTeamName()); // Sem time
        verify(playerRepository, times(1)).existsByEmail(playerRequestDTO.getEmail());
        verify(playerRepository, times(1)).save(any(Player.class));
        verify(teamRepository, never()).findById(anyLong()); // Não deve buscar time
    }

    @Test
    void testSaveFromDTO_Success_WithTeam() {
        playerRequestDTO.setTeamId(teamId);
        when(playerRepository.existsByEmail(anyString())).thenReturn(false);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player saved = invocation.getArgument(0);
            saved.setId(playerId);
            saved.setTeam(team); // Simula associação do time
            return saved;
        });

        PlayerResponseDTO responseDTO = playerService.saveFromDTO(playerRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(playerRequestDTO.getName(), responseDTO.getName());
        assertEquals(team.getName(), responseDTO.getTeamName());
        verify(playerRepository, times(1)).existsByEmail(playerRequestDTO.getEmail());
        verify(teamRepository, times(1)).findById(teamId);
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void testSaveFromDTO_EmailAlreadyExists() {
        when(playerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            playerService.saveFromDTO(playerRequestDTO);
        });
        verify(playerRepository, times(1)).existsByEmail(playerRequestDTO.getEmail());
        verify(teamRepository, never()).findById(anyLong());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testSaveFromDTO_TeamNotFound() {
        playerRequestDTO.setTeamId(teamId);
        when(playerRepository.existsByEmail(anyString())).thenReturn(false); // Email é único
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            playerService.saveFromDTO(playerRequestDTO);
        });
        verify(playerRepository, times(1)).existsByEmail(playerRequestDTO.getEmail());
        verify(teamRepository, times(1)).findById(teamId);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testFindById_Success() {
        player.setTeam(team); // Para testar o getTeamName no DTO
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        PlayerResponseDTO responseDTO = playerService.findById(playerId);

        assertNotNull(responseDTO);
        assertEquals(player.getName(), responseDTO.getName());
        assertEquals(team.getName(), responseDTO.getTeamName());
        verify(playerRepository, times(1)).findById(playerId);
    }
    
    @Test
    void testFindById_Success_FreeAgent() {
        player.setTeam(null); // Jogador sem time
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        PlayerResponseDTO responseDTO = playerService.findById(playerId);

        assertNotNull(responseDTO);
        assertEquals(player.getName(), responseDTO.getName());
        assertNull(responseDTO.getTeamName()); // Nome do time deve ser nulo
        verify(playerRepository, times(1)).findById(playerId);
    }


    @Test
    void testFindById_NotFound() {
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.findById(playerId);
        });
        verify(playerRepository, times(1)).findById(playerId);
    }

    @Test
    void testGetFreeAgents() {
        Player freeAgent = new Player();
        freeAgent.setId(2L);
        freeAgent.setName("Free Agent Player");
        freeAgent.setEmail("free@example.com");
        freeAgent.setTeam(null); // Importante

        when(playerRepository.findByTeamIsNull()).thenReturn(Collections.singletonList(freeAgent));

        List<PlayerResponseDTO> freeAgents = playerService.getFreeAgents();

        assertNotNull(freeAgents);
        assertFalse(freeAgents.isEmpty());
        assertEquals(1, freeAgents.size());
        assertEquals("Free Agent Player", freeAgents.get(0).getName());
        assertNull(freeAgents.get(0).getTeamName());
        verify(playerRepository, times(1)).findByTeamIsNull();
    }

    @Test
    void testDeleteById_Success() {
        when(playerRepository.existsById(playerId)).thenReturn(true);
        doNothing().when(playerRepository).deleteById(playerId);

        assertDoesNotThrow(() -> playerService.deleteById(playerId));

        verify(playerRepository, times(1)).existsById(playerId);
        verify(playerRepository, times(1)).deleteById(playerId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(playerRepository.existsById(playerId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.deleteById(playerId);
        });
        verify(playerRepository, times(1)).existsById(playerId);
        verify(playerRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindAll() {
        player.setTeam(team);
        when(playerRepository.findAll()).thenReturn(Collections.singletonList(player));

        List<PlayerResponseDTO> players = playerService.findAll();

        assertNotNull(players);
        assertFalse(players.isEmpty());
        assertEquals(1, players.size());
        assertEquals(player.getName(), players.get(0).getName());
        assertEquals(team.getName(), players.get(0).getTeamName());
        verify(playerRepository, times(1)).findAll();
    }
}