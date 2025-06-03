package com.ajs.arenasync.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.TeamRequestDTO;
import com.ajs.arenasync.DTO.TeamResponseDTO;
import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.TeamRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    // CREATE
    public TeamResponseDTO save(TeamRequestDTO dto) {
        if (teamRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Já existe um time com esse nome.");
        }

        Team team = new Team();
        team.setName(dto.getName());

        Team saved = teamRepository.save(team);
        return toResponseDTO(saved);
    }

    // READ
    public TeamResponseDTO findById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time", id));
        return toResponseDTO(team);
    }

    public Optional<Team> findOptionalById(Long id) {
        return teamRepository.findById(id);
    }

    // UPDATE
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