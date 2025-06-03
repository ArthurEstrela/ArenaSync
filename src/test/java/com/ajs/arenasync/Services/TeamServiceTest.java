package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.TeamRequestDTO;
import com.ajs.arenasync.DTO.TeamResponseDTO;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.TeamRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private TeamRequestDTO teamRequestDTO;
    private Long teamId = 1L;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(teamId);
        team.setName("Test Team");

        teamRequestDTO = new TeamRequestDTO();
        teamRequestDTO.setName("Test Team DTO");
    }

    @Test
    void testSave_Success() {
        when(teamRepository.existsByName(anyString())).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team saved = invocation.getArgument(0);
            saved.setId(teamId); // Simula a atribuição de ID
            return saved;
        });

        TeamResponseDTO responseDTO = teamService.save(teamRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(teamRequestDTO.getName(), responseDTO.getName());
        assertEquals(teamId, responseDTO.getId());
        verify(teamRepository, times(1)).existsByName(teamRequestDTO.getName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void testSave_NameAlreadyExists() {
        when(teamRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            teamService.save(teamRequestDTO);
        });

        verify(teamRepository, times(1)).existsByName(teamRequestDTO.getName());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testFindById_Success() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        TeamResponseDTO responseDTO = teamService.findById(teamId);

        assertNotNull(responseDTO);
        assertEquals(team.getName(), responseDTO.getName());
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void testFindById_NotFound() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.findById(teamId);
        });
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void testUpdate_Success() {
        TeamRequestDTO updateDto = new TeamRequestDTO();
        updateDto.setName("Updated Team Name");

        Team existingTeam = new Team(); // Simula o time existente no banco
        existingTeam.setId(teamId);
        existingTeam.setName("Old Team Name");

        Team updatedTeam = new Team(); // Simula o time após a atualização
        updatedTeam.setId(teamId);
        updatedTeam.setName(updateDto.getName());


        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        // Assume que o novo nome não conflita com outros times (exceto o próprio, se não mudar)
        when(teamRepository.existsByName(updateDto.getName())).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(updatedTeam);

        TeamResponseDTO responseDTO = teamService.update(teamId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(updateDto.getName(), responseDTO.getName());
        verify(teamRepository, times(1)).findById(teamId);
        verify(teamRepository, times(1)).existsByName(updateDto.getName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void testUpdate_TeamNotFound() {
        TeamRequestDTO updateDto = new TeamRequestDTO();
        updateDto.setName("Updated Team Name");

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.update(teamId, updateDto);
        });

        verify(teamRepository, times(1)).findById(teamId);
        verify(teamRepository, never()).existsByName(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testUpdate_NewNameAlreadyExistsForAnotherTeam() {
        TeamRequestDTO updateDto = new TeamRequestDTO();
        updateDto.setName("Existing Other Team Name"); // Nome que já pertence a outro time

        Team currentTeam = new Team();
        currentTeam.setId(teamId);
        currentTeam.setName("Original Team Name");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(currentTeam));
        when(teamRepository.existsByName(updateDto.getName())).thenReturn(true); // O "novo" nome já existe

        assertThrows(BadRequestException.class, () -> {
            teamService.update(teamId, updateDto);
        });

        verify(teamRepository, times(1)).findById(teamId);
        verify(teamRepository, times(1)).existsByName(updateDto.getName());
        verify(teamRepository, never()).save(any(Team.class));
    }
    
    @Test
    void testUpdate_NameUnchanged() {
        // Testa o caso em que o nome não é alterado.
        // A verificação de existsByName não deve causar um BadRequestException.
        TeamRequestDTO updateDto = new TeamRequestDTO();
        updateDto.setName(team.getName()); // Mesmo nome do time existente

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        //  Quando o nome é o mesmo do existente, a lógica no service é:
        // `!existingTeam.getName().equals(dto.getName()) && teamRepository.existsByName(dto.getName())`
        // A primeira parte `!existingTeam.getName().equals(dto.getName())` será `false`.
        // Então, `teamRepository.existsByName(dto.getName())` não precisa ser estritamente `false`
        // para passar, pois a condição `&&` não será satisfeita.
        // No entanto, para simplificar o mock, se o nome não muda, a chamada a existsByName para o *mesmo nome*
        // não deveria ser o motivo do erro. O save deve ocorrer normalmente.

        when(teamRepository.save(any(Team.class))).thenReturn(team); // Retorna o mesmo time, pois o nome não mudou

        TeamResponseDTO responseDTO = teamService.update(teamId, updateDto);

        assertNotNull(responseDTO);
        assertEquals(team.getName(), responseDTO.getName());
        verify(teamRepository, times(1)).findById(teamId);
        // `existsByName` não será chamado se o nome não mudou e a primeira condição do if falhar.
        // OU, se for chamado (dependendo da implementação exata do `if`),
        // ele retornaria `true` mas não deveria lançar exceção.
        // Na sua implementação atual: `!existingTeam.getName().equals(dto.getName())` é false.
        // Então, o `teamRepository.existsByName(dto.getName())` não é chamado para a verificação de conflito.
        verify(teamRepository, never()).existsByName(updateDto.getName()); // Ajustado: não deve checar se o nome não mudou
        verify(teamRepository, times(1)).save(any(Team.class));
    }


    @Test
    void testDeleteById_Success() {
        when(teamRepository.existsById(teamId)).thenReturn(true);
        doNothing().when(teamRepository).deleteById(teamId);

        assertDoesNotThrow(() -> teamService.deleteById(teamId));

        verify(teamRepository, times(1)).existsById(teamId);
        verify(teamRepository, times(1)).deleteById(teamId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(teamRepository.existsById(teamId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.deleteById(teamId);
        });

        verify(teamRepository, times(1)).existsById(teamId);
        verify(teamRepository, never()).deleteById(anyLong());
    }
}