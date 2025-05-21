package com.ajs.arenasync.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.TeamRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    // CREATE
    public Team save(Team team) {
        if (teamRepository.existsByName(team.getName())) {
            throw new BadRequestException("Já existe um time com esse nome.");
        }
        return teamRepository.save(team);
    }

    // READ
    public Team findById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time", id));
    }

    public Optional<Team> findOptionalById(Long id) {
        return teamRepository.findById(id);
    }

    // UPDATE
    public Team update(Long id, Team team) {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time", id));

        if (!existingTeam.getName().equals(team.getName()) &&
                teamRepository.existsByName(team.getName())) {
            throw new BadRequestException("Já existe outro time com esse nome.");
        }

        existingTeam.setName(team.getName());
        // Adicione aqui outros campos que podem ser atualizados, ex:
        // existingTeam.setCoach(team.getCoach());
        // existingTeam.setPlayers(team.getPlayers());

        return teamRepository.save(existingTeam);
    }

    // DELETE
    public void deleteById(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Time", id);
        }
        teamRepository.deleteById(id);
    }

}
