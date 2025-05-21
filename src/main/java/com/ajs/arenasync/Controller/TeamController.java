package com.ajs.arenasync.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajs.arenasync.Entities.Team;
import com.ajs.arenasync.Services.TeamService;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/{id}")
    public ResponseEntity<Team> findById(@PathVariable Long id) {
        Team obj = teamService.findById(id); // já lança exceção se não encontrar
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<Team> insert(@RequestBody Team team) {
        Team savedTeam = teamService.save(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable Long id, @RequestBody Team team) {
        Team existingTeam = teamService.findById(id); // valida se existe
        team.setId(existingTeam.getId());
        Team updatedTeam = teamService.save(team);
        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Team existingTeam = teamService.findById(id); // valida se existe
        teamService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
