package com.ajs.arenasync.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.TeamRequestDTO;
import com.ajs.arenasync.DTO.TeamResponseDTO;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.TeamRepository;

@Service
@CacheConfig(cacheNames = "teams") // Define o nome padrão do cache para essa classe
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    // CREATE
    @CacheEvict(allEntries = true) // Limpa o cache após criar novo time
    public TeamResponseDTO save(TeamRequestDTO dto) {
        if (teamRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Já existe um time com esse nome.");
        }

        Team team = new Team();
        team.setName(dto.getName());

        Team saved = teamRepository.save(team);
        return toResponseDTO(saved);
    }

    // READ by ID
    @Cacheable(key = "#id")
    public TeamResponseDTO findById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time", id));
        return toResponseDTO(team);
    }

    public Optional<Team> findOptionalById(Long id) {
        return teamRepository.findById(id);
    }

    // READ ALL
    @Cacheable
    public List<TeamResponseDTO> findAll() {
        return teamRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    @CachePut(key = "#id")
    @CacheEvict(key = "'findAll'", beforeInvocation = true) // limpa o cache do findAll
    public TeamResponseDTO update(Long id, TeamRequestDTO dto) {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time", id));

        if (!existingTeam.getName().equals(dto.getName()) &&
                teamRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Já existe outro time com esse nome.");
        }

        existingTeam.setName(dto.getName());

        Team updated = teamRepository.save(existingTeam);
        return toResponseDTO(updated);
    }

    // DELETE
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'findAll'")
    })
    public void deleteById(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Time", id);
        }
        teamRepository.deleteById(id);
    }

    // Conversão para ResponseDTO
    private TeamResponseDTO toResponseDTO(Team team) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        return dto;
    }
}
