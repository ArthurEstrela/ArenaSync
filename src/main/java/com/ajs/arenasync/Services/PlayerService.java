package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.PlayerRequestDTO;
import com.ajs.arenasync.DTO.PlayerResponseDTO;
import com.ajs.arenasync.Entities.Player;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PlayerRepository;
import com.ajs.arenasync.Repositories.TeamRepository;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    public PlayerResponseDTO saveFromDTO(PlayerRequestDTO dto) {
        if (playerRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Já existe um jogador com esse e-mail.");
        }

        Player player = toEntity(dto);
        Player saved = playerRepository.save(player);
        return toResponseDTO(saved);
    }

    public PlayerResponseDTO findById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
        return toResponseDTO(player);
    }

    public List<PlayerResponseDTO> getFreeAgents() {
        return playerRepository.findByTeamIsNull().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Player", id);
        }
        playerRepository.deleteById(id);
    }

    public List<PlayerResponseDTO> findAll() {
        return playerRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Conversão de DTO para Entidade
    private Player toEntity(PlayerRequestDTO dto) {
        Player player = new Player();
        player.setName(dto.getName());
        player.setEmail(dto.getEmail());

        if (dto.getTeamId() != null) {
            Team team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new BadRequestException("Time informado não encontrado."));
            player.setTeam(team);
        }

        return player;
    }

    // Conversão de Entidade para DTO
    private PlayerResponseDTO toResponseDTO(Player player) {
        PlayerResponseDTO dto = new PlayerResponseDTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setEmail(player.getEmail());
        dto.setTeamName(player.getTeam() != null ? player.getTeam().getName() : null);
        return dto;
    }
}
